package com.skylab.superapp.core.utilities.media;

import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.exceptions.ValidationException;
import io.github.borewit.sanitize.SVGSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;


@Slf4j
@Component
public class MediaSanitizer {

    public record SanitizedMedia(byte[] data, String contentType) {}

    private static final Set<String> PNG_KEEP_ANCILLARY =
            Set.of("tRNS", "gAMA", "cHRM", "sRGB", "iCCP", "bKGD", "pHYs", "sBIT", "hIST");

    public SanitizedMedia sanitize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new ValidationException("Boş medya yüklenemez.");
        }
        if (isJpeg(bytes)) {
            return new SanitizedMedia(stripJpegMetadata(bytes), "image/jpeg");
        }
        if (isPng(bytes)) {
            return new SanitizedMedia(stripPngMetadata(bytes), "image/png");
        }
        if (isWebp(bytes)) {
            return new SanitizedMedia(stripWebpMetadata(bytes), "image/webp");
        }
        if (isGif(bytes)) {
            return new SanitizedMedia(stripGifMetadata(bytes), "image/gif");
        }
        if (isSvg(bytes)) {
            return new SanitizedMedia(sanitizeSvg(bytes), "image/svg+xml");
        }
        throw new ValidationException("Desteklenmeyen veya tanınamayan medya türü (yalnızca JPEG, PNG, WebP, GIF, SVG).");
    }


    private boolean isJpeg(byte[] b) {
        return b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
    }

    private boolean isPng(byte[] b) {
        return b.length >= 8
                && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && (b[4] & 0xFF) == 0x0D && (b[5] & 0xFF) == 0x0A && (b[6] & 0xFF) == 0x1A && (b[7] & 0xFF) == 0x0A;
    }

    private boolean isWebp(byte[] b) {
        return b.length >= 12
                && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
    }

    private boolean isGif(byte[] b) {
        return b.length >= 6
                && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private boolean isSvg(byte[] b) {
        int n = Math.min(b.length, 1024);
        String head = new String(b, 0, n, StandardCharsets.UTF_8).toLowerCase();
        return head.contains("<svg");
    }


    private byte[] stripJpegMetadata(byte[] b) {
        if (b.length < 2 || (b[0] & 0xFF) != 0xFF || (b[1] & 0xFF) != 0xD8) {
            throw new BusinessException("Geçersiz JPEG.");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(b.length);
        out.write(0xFF);
        out.write(0xD8); // SOI
        int pos = 2;
        while (pos + 4 <= b.length) {
            if ((b[pos] & 0xFF) != 0xFF) {
                break;
            }
            int marker = b[pos + 1] & 0xFF;
            if (marker == 0xDA) {
                out.write(b, pos, b.length - pos);
                return out.toByteArray();
            }
            int segLen = ((b[pos + 2] & 0xFF) << 8) | (b[pos + 3] & 0xFF);
            int segTotal = 2 + segLen;
            if (segLen < 2 || pos + segTotal > b.length) {
                break;
            }
            boolean drop = marker == 0xE1
                    || marker == 0xED
                    || marker == 0xFE;
            if (!drop) {
                out.write(b, pos, segTotal);
            }
            pos += segTotal;
        }
        if (pos < b.length) {
            out.write(b, pos, b.length - pos);
        }
        return out.toByteArray();
    }



    private byte[] stripPngMetadata(byte[] bytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
        out.write(bytes, 0, 8);
        int pos = 8;
        while (pos + 8 <= bytes.length) {
            int len = readUInt32(bytes, pos);
            if (len < 0 || pos + 12L + len > bytes.length) {
                break;
            }
            String type = new String(bytes, pos + 4, 4, StandardCharsets.US_ASCII);
            int chunkTotal = 12 + len;

            boolean critical = Character.isUpperCase(type.charAt(0));
            boolean keep = critical || PNG_KEEP_ANCILLARY.contains(type);
            if (keep) {
                out.write(bytes, pos, chunkTotal);
            }
            pos += chunkTotal;
            if ("IEND".equals(type)) {
                break;
            }
        }
        return out.toByteArray();
    }

    private int readUInt32(byte[] b, int off) {
        return ((b[off] & 0xFF) << 24) | ((b[off + 1] & 0xFF) << 16)
                | ((b[off + 2] & 0xFF) << 8) | (b[off + 3] & 0xFF);
    }



    private byte[] stripWebpMetadata(byte[] b) {
        if (b.length < 12) {
            throw new BusinessException("Geçersiz WebP.");
        }
        ByteArrayOutputStream body = new ByteArrayOutputStream(b.length);
        int pos = 12; // "RIFF"(4) + size(4) + "WEBP"(4)
        while (pos + 8 <= b.length) {
            String fourcc = new String(b, pos, 4, StandardCharsets.US_ASCII);
            int size = (b[pos + 4] & 0xFF) | ((b[pos + 5] & 0xFF) << 8)
                    | ((b[pos + 6] & 0xFF) << 16) | ((b[pos + 7] & 0xFF) << 24);
            if (size < 0) {
                break;
            }
            int chunkTotal = 8 + size + (size & 1); // header + payload + (varsa) pad
            if (pos + chunkTotal > b.length) {
                chunkTotal = b.length - pos;
            }

            boolean drop = "EXIF".equals(fourcc) || "XMP ".equals(fourcc);
            if (!drop) {
                if ("VP8X".equals(fourcc) && chunkTotal >= 9) {
                    byte[] chunk = Arrays.copyOfRange(b, pos, pos + chunkTotal);
                    chunk[8] = (byte) (chunk[8] & ~0x0C);
                    body.write(chunk, 0, chunk.length);
                } else {
                    body.write(b, pos, chunkTotal);
                }
            }
            pos += chunkTotal;
        }

        byte[] bodyBytes = body.toByteArray();
        int riffSize = 4 + bodyBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream(12 + bodyBytes.length);
        out.write('R'); out.write('I'); out.write('F'); out.write('F');
        out.write(riffSize & 0xFF); out.write((riffSize >> 8) & 0xFF);
        out.write((riffSize >> 16) & 0xFF); out.write((riffSize >> 24) & 0xFF);
        out.write('W'); out.write('E'); out.write('B'); out.write('P');
        out.write(bodyBytes, 0, bodyBytes.length);
        return out.toByteArray();
    }


    private byte[] stripGifMetadata(byte[] b) {
        if (b.length < 13) {
            throw new BusinessException("Geçersiz GIF.");
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(b.length);
            out.write(b, 0, 13); // header(6) + logical screen descriptor(7)
            int pos = 13;

            int packed = b[10] & 0xFF;
            if ((packed & 0x80) != 0) { // global color table
                int gctSize = 3 * (1 << ((packed & 0x07) + 1));
                if (pos + gctSize > b.length) return b;
                out.write(b, pos, gctSize);
                pos += gctSize;
            }

            while (pos < b.length) {
                int blockId = b[pos] & 0xFF;
                if (blockId == 0x3B) { // trailer
                    out.write(0x3B);
                    break;
                } else if (blockId == 0x2C) { // image descriptor
                    if (pos + 10 > b.length) return b;
                    int imgPacked = b[pos + 9] & 0xFF;
                    int p = pos + 10;
                    if ((imgPacked & 0x80) != 0) {
                        p += 3 * (1 << ((imgPacked & 0x07) + 1));
                    }
                    if (p + 1 > b.length) return b;
                    p += 1;
                    p = skipGifSubBlocks(b, p);
                    if (p < 0) return b;
                    out.write(b, pos, p - pos);
                    pos = p;
                } else if (blockId == 0x21) { // extension
                    if (pos + 2 > b.length) return b;
                    int label = b[pos + 1] & 0xFF;
                    int dataStart = pos + 2;
                    int end = skipGifSubBlocks(b, dataStart);
                    if (end < 0) return b;
                    boolean drop = (label == 0xFE)
                            || (label == 0xFF && isGifXmpApp(b, dataStart));
                    if (!drop) {
                        out.write(b, pos, end - pos);
                    }
                    pos = end;
                } else {
                    return b;
                }
            }
            return out.toByteArray();
        } catch (Exception e) {
            log.warn("GIF metadata strip failed, returning original. Error: {}", e.getMessage());
            return b;
        }
    }


    private int skipGifSubBlocks(byte[] b, int p) {
        while (p < b.length) {
            int len = b[p] & 0xFF;
            p += 1;
            if (len == 0) return p;
            p += len;
        }
        return -1;
    }


    private boolean isGifXmpApp(byte[] b, int p) {
        if (p >= b.length || (b[p] & 0xFF) != 11 || p + 12 > b.length) return false;
        String appId = new String(b, p + 1, 11, StandardCharsets.US_ASCII);
        return appId.startsWith("XMP Data");
    }

    private byte[] sanitizeSvg(byte[] bytes) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            SVGSanitizer.sanitize(new ByteArrayInputStream(bytes), out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("SVG sanitize failed. Error: {}", e.getMessage());
            throw new BusinessException("SVG temizlenemedi.");
        }
    }
}

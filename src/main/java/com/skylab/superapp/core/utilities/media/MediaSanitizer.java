package com.skylab.superapp.core.utilities.media;

import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.exceptions.ValidationException;
import io.github.borewit.sanitize.SVGSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
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
        if (isSvg(bytes)) {
            return new SanitizedMedia(sanitizeSvg(bytes), "image/svg+xml");
        }
        throw new ValidationException("Desteklenmeyen veya tanınamayan medya türü (yalnızca JPEG, PNG, SVG).");
    }


    private boolean isJpeg(byte[] b) {
        return b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
    }

    private boolean isPng(byte[] b) {
        return b.length >= 8
                && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && (b[4] & 0xFF) == 0x0D && (b[5] & 0xFF) == 0x0A && (b[6] & 0xFF) == 0x1A && (b[7] & 0xFF) == 0x0A;
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

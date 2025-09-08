package com.skylab.superapp.core.utilities.qr;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZxingQRCodeManager implements QRCodeService {

    @Override
    public byte[] generateQRCode(String data, int width, int height) throws IOException {
        try {
            BufferedImage qrImage = createCircularQRCode(data, width, height, null);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (WriterException e) {
            throw new IOException("Error while generating QRCode: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generateQRCodeWithLogo(String data, int width, int height, String logoPath, int dummy) throws IOException {
        try {
            int[] finderPatternPixelSize = new int[1];
            BufferedImage qrImage = createCircularQRCode(data, width, height, finderPatternPixelSize);
            BufferedImage qrWithLogo = addLogoToQRCode(qrImage, logoPath, finderPatternPixelSize[0]);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrWithLogo, "PNG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (WriterException e) {
            throw new IOException("Error while generating QRCode: " + e.getMessage(), e);
        }
    }

    private BufferedImage createCircularQRCode(String data, int width, int height, int[] finderPatternPixelSizeOut) throws WriterException {
        final Map<EncodeHintType, Object> encodingHints = new HashMap<>();
        encodingHints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        QRCode code = Encoder.encode(data, ErrorCorrectionLevel.H, encodingHints);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);

        ByteMatrix input = code.getMatrix();
        if (input == null) {
            throw new IllegalStateException();
        }
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();
        int quietZone = 4;
        int qrWidth = inputWidth + (quietZone * 2);
        int qrHeight = inputHeight + (quietZone * 2);
        int outputWidth = Math.max(width, qrWidth);
        int outputHeight = Math.max(height, qrHeight);

        int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
        int topPadding = (outputHeight - (inputHeight * multiple)) / 2;
        final int FINDER_PATTERN_SIZE = 7;
        final float CIRCLE_SCALE_DOWN_FACTOR = 0.85f;
        int circleSize = (int) (multiple * CIRCLE_SCALE_DOWN_FACTOR);

        int finderPatternPixelSize = multiple * FINDER_PATTERN_SIZE;
        if (finderPatternPixelSizeOut != null && finderPatternPixelSizeOut.length > 0) {
            finderPatternPixelSizeOut[0] = finderPatternPixelSize;
        }

        for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
            for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
                if (input.get(inputX, inputY) == 1) {
                    if (!(inputX <= FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE ||
                            inputX >= inputWidth - FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE ||
                            inputX <= FINDER_PATTERN_SIZE && inputY >= inputHeight - FINDER_PATTERN_SIZE)) {
                        graphics.fillRect(outputX, outputY, circleSize, circleSize);
                    }
                }
            }
        }

        drawFinderPatternCircleStyle(graphics, leftPadding, topPadding, finderPatternPixelSize);
        drawFinderPatternCircleStyle(graphics, leftPadding + (inputWidth - FINDER_PATTERN_SIZE) * multiple, topPadding, finderPatternPixelSize);
        drawFinderPatternCircleStyle(graphics, leftPadding, topPadding + (inputHeight - FINDER_PATTERN_SIZE) * multiple, finderPatternPixelSize);

        graphics.dispose();
        return image;
    }

    private void drawFinderPatternCircleStyle(Graphics2D graphics, int x, int y, int size) {
        final int WHITE_SIZE = size * 5 / 7;
        final int WHITE_OFFSET = size / 7;
        final int MIDDLE_SIZE = size * 3 / 7;
        final int MIDDLE_OFFSET = size * 2 / 7;

        graphics.setColor(Color.BLACK);
        graphics.fillRect(x, y, size, size);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(x + WHITE_OFFSET, y + WHITE_OFFSET, WHITE_SIZE, WHITE_SIZE);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(x + MIDDLE_OFFSET, y + MIDDLE_OFFSET, MIDDLE_SIZE, MIDDLE_SIZE);
    }

    private BufferedImage addLogoToQRCode(BufferedImage qrCodeImage, String logoPath, int logoSize) throws IOException {
        ClassPathResource logoResource = new ClassPathResource(logoPath);
        InputStream logoStream = logoResource.getInputStream();
        BufferedImage logoImage = ImageIO.read(logoStream);

        Image scaledLogo = logoImage.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
        BufferedImage resizedLogo = new BufferedImage(logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedLogo.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(scaledLogo, 0, 0, null);
        g2d.dispose();

        Graphics2D qrGraphics = qrCodeImage.createGraphics();
        qrGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int backgroundX = (qrCodeImage.getWidth() - logoSize) / 2;
        int backgroundY = (qrCodeImage.getHeight() - logoSize) / 2;
        qrGraphics.setColor(Color.WHITE);
        qrGraphics.fillRect(backgroundX, backgroundY, logoSize, logoSize);

        qrGraphics.drawImage(resizedLogo, backgroundX, backgroundY, null);
        qrGraphics.dispose();

        return qrCodeImage;
    }
}
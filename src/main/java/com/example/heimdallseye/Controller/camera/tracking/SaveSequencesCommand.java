/*
 * The MIT License
 *
 * Copyright 2016 Maurice Amon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.example.heimdallseye.Controller.camera.tracking;


import com.example.heimdallseye.Controller.Command;
import com.example.heimdallseye.Model.Frame;
import com.example.heimdallseye.Model.entities.SequenceEntity;
import com.example.heimdallseye.Model.entities.UnitOfWorkDatabaseMapper;
import com.example.heimdallseye.View.factory.widgets.VideoStreamThumbnail;
import com.example.heimdallseye.View.factory.windows.VideoStreamWindow;
import com.example.heimdallseye.heimdallseye.observer.Observer;
import io.humble.video.Codec;
import io.humble.video.Encoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import static io.humble.video.awt.MediaPictureConverterFactory.convertToType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * THIS CLASS HAS THE FUNCTION TO SAVE THE FRAME-SEQUENCES, WHILE THE
 * OBJECT-TRACKING-MODULE IS ACTIVE.
 *
 * @author Maurice Amon
 * @version 1.1
 */
public class SaveSequencesCommand extends Command implements Observer {

    private final UnitOfWorkDatabaseMapper UNIT_OF_WORK_DATABASE_MAPPER = UnitOfWorkDatabaseMapper.getUnitOfWorkMapperInstance();
    
    private final Integer FRAME_RESOLUTION_X = 1920;
    
    private final Integer FRAME_RESOLUTION_Y = 1080;

    private SequenceEntity sequenceEntity;

    private Integer frameCounter = 0;

    private final Frame FRAME;

    private final Rational FRAME_RATE = Rational.make(1, 30);

    private Encoder encoder;

    private Muxer muxer;

    private final MediaPacket MEDIA_PACKET = MediaPacket.make();

    private MediaPicture mediaPicture;

    private MediaPictureConverter converter;

    public SaveSequencesCommand() {
        persistSequenceEntity();
        prepareVideoRecording();
        Integer active = 0;
        VideoStreamWindow window = VideoStreamWindow.getInstance(null, null);
        for (VideoStreamThumbnail thumbnail : window.getThumbnailElements()) {
            if (thumbnail.isActive()) {
                active = window.getThumbnailElements().indexOf(thumbnail);
            }
        }
        this.FRAME = window.getThumbnailElements().get(active).getStream().frame;
        this.FRAME.addListener(this);
    }

    @Override
    public void executeCommand() {
    }

    private void persistSequenceEntity() {
        Date date = new Date();
        sequenceEntity = new SequenceEntity();
        sequenceEntity.setDate(date);
        UNIT_OF_WORK_DATABASE_MAPPER.addObjectToInsertList(sequenceEntity);
        UNIT_OF_WORK_DATABASE_MAPPER.commit();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        new File(sequenceEntity.getRelativePath() + format.format(sequenceEntity.getDate())).mkdirs();
    }

    private void prepareVideoRecording() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = format.format(sequenceEntity.getDate());
        muxer = Muxer.make(sequenceEntity.getRelativePath() + formatDate + "/" + formatDate + ".mp4", null, null);

        MuxerFormat muxerFormat = muxer.getFormat();
        Codec codec = Codec.findEncodingCodec(muxerFormat.getDefaultVideoCodecId());

        encoder = Encoder.make(codec);
        encoder.setWidth(FRAME_RESOLUTION_X);
        encoder.setHeight(FRAME_RESOLUTION_Y);

        PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(FRAME_RATE);
        if (muxerFormat.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
        }
        encoder.open(null, null);
        muxer.addNewStream(encoder);

        try {
            muxer.open(null, null);
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(SaveSequencesCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        converter = null;
        mediaPicture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixelformat);
        mediaPicture.setTimeBase(FRAME_RATE);
    }

    public void stopRecording() {
        FRAME.removeListener(this);
        do {
            encoder.encode(MEDIA_PACKET, null);
            if (MEDIA_PACKET.isComplete()) {
                muxer.write(MEDIA_PACKET, false);
            }
        } while (MEDIA_PACKET.isComplete());
        muxer.close();
    }

    /* Listen to the Frame-Object and persists every Frame on the filesystem ...
       @param object (Cast to image) 
     */
    @Override
    public void update(Object object) {
        BufferedImage img = convertToType(SwingFXUtils.fromFXImage((Image) object, null), BufferedImage.TYPE_3BYTE_BGR);
        if (converter == null) {
            converter = MediaPictureConverterFactory.createConverter(img, mediaPicture);
        }
        converter.toPicture(mediaPicture, img, frameCounter);
        do {
            encoder.encode(MEDIA_PACKET, mediaPicture);
            if (MEDIA_PACKET.isComplete()) {
                muxer.write(MEDIA_PACKET, false);
            }
        } while (MEDIA_PACKET.isComplete());
        frameCounter++;
    }
}

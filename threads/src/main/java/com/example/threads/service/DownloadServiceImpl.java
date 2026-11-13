package com.example.threads.service;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Channels;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DownloadServiceImpl {

    public void download(String url, Path destination) throws Exception {

        var remoteUrl = URI.create(url).toURL();

        try (ReadableByteChannel source = Channels.newChannel(remoteUrl.openStream());
                FileChannel target = FileChannel.open(
                        destination,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING)) {

            ByteBuffer buffer = ByteBuffer.allocate(8192);

            while (source.read(buffer) != -1) {
                buffer.flip();

                while (buffer.hasRemaining()) {
                    target.write(buffer);
                }

                buffer.clear();
            }
        }
    }
}
package com.nxs.channel;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

/**
 * 一、通道（Channel）: 用于源节点与目标节点的连接，在java nio中负责缓冲区数据中数据的传输。channel本身不存储数据，因此需要配合缓冲区进行传输
 * 主要实现类
 * FileChannel  本地文件
 * SocketChannel   TCP
 * ServerSocketChannel  TCP
 * DatagramChannel  UDP
 * 二、获取通道
 * 1.getChannel()
 * 本地IO
 * FileInputStream/FileOutStream
 * RandomAccessFile
 * 网络IO
 * Socket
 * ServerSocket
 * DatagramSocket
 * 2.open()
 * 3.newByteChannel()
 * 三、通道之间数据传输
 * transferTo()
 * transferFrom()
 * 四、分散(Scatter)与聚集(Gather)
 * 分散读取（Scattering Reads）:将通道中的数据依次按顺序分散到多个缓冲区中
 * 聚集写入（Gathering Write）:将缓冲区中的数据依次按顺序聚集到通道中
 * 五、字符集：Charset
 * 编码:字符串 -> 字节数组
 * 解码:字节数组 -> 字符串
 */
public class TestChannel {

    /**
     * 1.利用通道完成文件的复制
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        FileInputStream fis = new FileInputStream("1.jpg");
        FileOutputStream fos = new FileOutputStream("2.jpg");

        //获取通道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        //分配指定大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //将通道中的数据存入缓冲区
        while (inChannel.read(buffer) != -1) {
            //切换读取数据的模式
            buffer.flip();
            //将缓冲区的数据写入通道中
            outChannel.write(buffer);
            //清空缓冲区
            buffer.clear();
        }
        outChannel.close();
        inChannel.close();
        fos.close();
        fis.close();
    }

    /**
     * 2.使用直接缓冲区完成文件的复制（内存映射文件）
     */
    @Test
    public void test2() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("2.png"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
        //内存映射文件
        MappedByteBuffer inMapBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMapBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
        //直接对缓冲区进行数据的读写操作
        byte[] dst = new byte[inMapBuffer.limit()];
        inMapBuffer.get(dst);
        outMapBuffer.put(dst);
        inChannel.close();
        outChannel.close();
    }

    /**
     * 3.通道之间数据传输(直接缓冲区)
     */
    @Test
    public void test3() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("2.png"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
//        inChannel.transferTo(0, inChannel.size(), outChannel);
        outChannel.transferFrom(inChannel, 0, inChannel.size());
        inChannel.close();
        outChannel.close();
    }

    /**
     * 分散读取，聚集写入
     * @throws IOException
     */
    @Test
    public void test4() throws IOException {
        RandomAccessFile inFile = new RandomAccessFile("1.txt", "rw");
        //1.获取通道
        FileChannel inChannel = inFile.getChannel();
        //2.分配指定大小的缓冲区
        ByteBuffer buffer1 = ByteBuffer.allocate(100);
        ByteBuffer buffer2 = ByteBuffer.allocate(1024);

        //3.分散读取
        ByteBuffer[] byteBuffers = {buffer1, buffer2};
        inChannel.read(byteBuffers);
        for (ByteBuffer byteBuffer : byteBuffers) {
            byteBuffer.flip();
        }
        System.out.println(new String(byteBuffers[0].array(),0,byteBuffers[0].limit()));
        System.out.println(new String(byteBuffers[1].array(), 0, byteBuffers[1].limit()));
        //3.聚集写入
        RandomAccessFile outFile = new RandomAccessFile("2.txt", "rw");
        FileChannel outChannel = outFile.getChannel();
        outChannel.write(byteBuffers);
    }

    @Test
    public void test5(){
        Map<String, Charset> map = Charset.availableCharsets();
        Set<Map.Entry<String, Charset>> entries = map.entrySet();
        for (Map.Entry<String, Charset> entry : entries) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    @Test
    public void test6() throws CharacterCodingException {
        Charset gbk = Charset.forName("GBK");
        //获取编码器与解码器
        CharsetEncoder charsetEncoder = gbk.newEncoder();
        CharsetDecoder charsetDecoder = gbk.newDecoder();
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("你好，中国！");
        charBuffer.flip();
        ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);
        for (int i = 0; i < 12; i++) {
            System.out.println(byteBuffer.get());
        }
        byteBuffer.flip();
//        byteBuffer.rewind();
        //解码
        CharBuffer decodeBuffer = charsetDecoder.decode(byteBuffer);
        System.out.println(decodeBuffer.toString());
    }
}

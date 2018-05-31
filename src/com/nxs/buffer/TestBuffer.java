package com.nxs.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 缓冲区（Buffer）：在java nio中负责数据的存取。缓冲区就是数组，用存储不同数据类型的数据
 * 根据数据类型不同（boolean 除外），提供了相应类型的缓冲区
 * ByteBuffer （最常用）
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * 上述缓冲区的管理方式几乎一致，通过allocate()获取缓冲区
 *
 * 1.缓冲区存取数据的两个核心方法
 * put() ： 存入数据到缓冲区
 * get() : 获取缓冲区中的数据
 * 2.缓冲区中的四个核心属性
 * capacity : 容量，表示缓冲区中最大存储数据的容量，一旦声明不能改变
 * limit : 界限 ，表示缓冲区可以操作数据的大小（limit 后数据不能进行读写)
 * position : 位置，表示缓冲区正在操作的数据的位置
 *
 * mark ：标记，表示记录当前position的位置，可以通过reset()恢复到mark的位置
 * 0 <= mark <= position <= limit <= capacity
 *
 * 3.直接缓冲区与非直接缓冲区
 * 非直接 通过allocate()方法分配缓冲区，将缓冲区建立在jvm中
 * 直接 通过allocateDirect()方法分配至直接缓冲区，将缓冲区建立在物理内存中，可以提高效率
 */
public class TestBuffer {

    /**
     * 关于flip，看到JDK的文档大概是这么说的：“将limit属性设置为当前的位置”；而关于rewind方法，是在limit属性已经被设置合适的情况下使用的。
     * 也就是说这两个方法虽然都能够使指针返回到缓冲区的第一个位置，但是flip在调整指针之前，将limit属性设置为当前位置。
     */

    @Test
    public void test1(){
        String string = "abcde";
        //1.分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //2.使用put（）存入数据到缓冲区
        buf.put(string.getBytes());

        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());
        //3.Flips this buffer. The limit is set to the current position and then the position is set to zero. If the mark is defined then it is discarded
        buf.flip();

        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());
        //4.利用get()读取缓冲区中的数据
        byte[] dst = new byte[buf.limit()-1];
        buf.get(dst);
        System.out.println(new String(dst, 0, dst.length));
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());


        buf.flip();

        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());
        //4.利用get()读取缓冲区中的数据
        byte[] dst2 = new byte[buf.limit()];
        buf.get(dst2);
        System.out.println(new String(dst, 0, dst2.length));
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //5.rewind() 可重读
        buf.rewind();

        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //6.clear():清空缓冲区,d但是缓冲区中的数据依然存在，但是出于"被遗忘"状态
        buf.clear();

        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        System.out.println((char) buf.get());
    }

    @Test
    public void test2(){
        String string = "abcde";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(string.getBytes());
        buf.flip();
        byte[] dst = new byte[buf.limit()];
        buf.get(dst,0,2);
        System.out.println(new String(dst, 0, 2));
        System.out.println(buf.position());
        //mark() : 标记
        buf.mark();
        buf.get(dst, 2, 2);
        System.out.println(new String(dst, 0, 2));
        System.out.println(buf.position());

        //reset() ： 恢复到mark的位置
        buf.reset();
        System.out.println(buf.position());

        //判断缓冲区是否还有剩余
        if(buf.hasRemaining()){
            //获取缓冲区剩余的数据
            System.out.println(buf.remaining());
        }


    }

    @Test
    public void test3(){
        //分配直接缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        System.out.println(byteBuffer.isDirect());

    }
}

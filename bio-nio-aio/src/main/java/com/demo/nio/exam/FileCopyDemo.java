package com.demo.nio.exam;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Classname FileCopyDemo
 * @Description
 * @Date 2020/3/26 22:12
 * @Created by lyf
 */
interface FileCopyRunner{
    void copyFile(File source, File target);
}
public class FileCopyDemo {
    private static final  int ROUNDS = 5;
    private static void benchMark(FileCopyRunner fileCopyRunner,File source,File target){
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < ROUNDS; i++) {
            fileCopyRunner.copyFile(source,target);
            target.delete();
        }
        long time = (System.currentTimeMillis() - startTime)/ROUNDS;
        System.out.println(fileCopyRunner.toString()+":"+time);
    }
    public static void close(Closeable closeable){
        if (closeable!=null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        FileCopyRunner noBufferStreamCopy = new FileCopyRunner() {
            @Override
            public String toString() {
                return "noBufferStreamCopy";
            }

            @Override
            public void copyFile(File source, File target) {
                InputStream fin = null;
                OutputStream fout = null;
                try {
                    fin = new FileInputStream(source);
                    fout = new FileOutputStream(target);
                    int result ;
                    while ((result=fin.read())!=-1){
                        fout.write(result);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    close(fin);
                    close(fout);
                }
            }
        };
        FileCopyRunner bufferStreamCopy = new FileCopyRunner() {
            @Override
            public String toString() {
                return "bufferStreamCopy";
            }
            @Override
            public void copyFile(File source, File target) {
                InputStream fin = null;
                OutputStream fout = null;
                try {
                    fin = new BufferedInputStream(new FileInputStream(source));
                    fout = new BufferedOutputStream(new FileOutputStream(target));
                    byte[] buffer = new byte[1024];
                    int result ;
                    while((result = fin.read(buffer))!=-1){
                        fout.write(buffer,0,result);
                        fout.flush();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    close(fin);
                    close(fout);
                }
            }
        };
        FileCopyRunner niobufferCopy = new FileCopyRunner() {
            @Override
            public String toString() {
                return "niobufferCopy";
            }
            @Override
            public void copyFile(File source, File target) {
                FileChannel fin = null;
                FileChannel fout = null;
                try {
                    fin = new FileInputStream(source).getChannel();
                    fout = new FileOutputStream(target).getChannel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int result ;
                    while((result = fin.read(buffer))!=-1){
                        buffer.flip();//写改读
                        while(buffer.hasRemaining()) {
                            fout.write(buffer);
                        }
                        buffer.clear();//读转写
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    close(fin);
                    close(fout);
                }
            }
        };
        FileCopyRunner nioTransferCopy = new FileCopyRunner() {
            @Override
            public String toString() {
                return "nioTransferCopy";
            }
            @Override
            public void copyFile(File source, File target) {
                FileChannel fin = null;
                FileChannel fout = null;
                try {
                    fin = new FileInputStream(source).getChannel();
                    fout = new FileOutputStream(target).getChannel();
                    long transfered = 0l;
                    long size = fin.size();
                    while (transfered!= size) {
                        transfered += fin.transferTo(0, size, fout);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    close(fin);
                    close(fout);
                }
            }
        };
        File file = new File("C:\\Users\\lyf\\Desktop\\Beyond-海阔天空.mp3");
        File file1 = new File("C:\\Users\\lyf\\Desktop\\Beyond-海阔天空1.mp3");
        System.out.println("开始");
       // benchMark(noBufferStreamCopy,file,file1);
        benchMark(bufferStreamCopy,file,file1);//10m 209s
        benchMark(niobufferCopy,file,file1);//10m 244s
        benchMark(nioTransferCopy,file,file1);//10m 96s
    }
}

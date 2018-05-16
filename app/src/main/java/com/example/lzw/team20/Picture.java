package com.example.lzw.team20;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Myh on 2018/4/27.
 */

public class Picture {

    // int []rgb=new int[3];
    int width;
    int height;


    // int [][]allRgb;
    int[][] Lbpgray;
    int[][] gray;
    int minx;
    int miny;
    int [][]histogram;
    List<Double> histograms;
    //
    int neighbors = 8;
    //x方向有多少块
    int gradx = 8;
    //y方向有多少块
    int grady = 8;

    public static Picture setRgb(Bitmap bt){
        Picture pic =new Picture();
        pic.width = bt.getWidth();
        pic.height = bt.getHeight();
        // pic.minx = bt.getMinX();
        // pic.miny = bt.getMinY();
        int rgb[]=new int[3];
        pic.gray=new int[pic.width][pic.height];
        for(int i=0;i<pic.width;i++){
            for(int j=0;j<pic.height;j++){
                int pixel=bt.getPixel(i,j);
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);

                pic.gray[i][j] = (int) (rgb[0] * 0.3 + rgb[1] * 0.59 + rgb[2] * 0.11);

            }
        }
        return pic;
    }

    public int judge(int i, int j) {
        if (i >= j) {
            return 1;
        } else {
            return 0;
        }
    }

    //
    public Picture getLbp(Picture pic) {

        int neighborhood[] = new int[8];
        Picture currPic = pic;
        int width = currPic.width;
        int height = currPic.height;
        int centre;
        //计数
        int count = 0;

        currPic.Lbpgray = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i == 0 || i == width - 1 || j == 0 || j == height) {
                    currPic.Lbpgray[i][j] = currPic.gray[i][j];
                } else {
                    centre = currPic.gray[i][j];

                    neighborhood[0] = currPic.gray[i - 1][j - 1];
                    neighborhood[1] = currPic.gray[i - 1][j];
                    neighborhood[2] = currPic.gray[i - 1][j + 1];
                    neighborhood[3] = currPic.gray[i][j - 1];
                    neighborhood[4] = currPic.gray[i][j + 1];
                    neighborhood[5] = currPic.gray[i + 1][j - 1];
                    neighborhood[6] = currPic.gray[i + 1][j];
                    neighborhood[7] = currPic.gray[i + 1][j = 1];

                    //比较中心点和周围点rgb值的大小
                    List<Integer> binaryList = new ArrayList<>();
                    for (int n = 0; n < 8; n++) {
                        binaryList.add(judge(neighborhood[n], centre));
                    }

                    double value = 0.0;
                    for (int n = 0; n < 8; n++) {
                        value += binaryList.get(n) * Math.pow(2, 7 - n);
                    }
                    currPic.Lbpgray[i][j] = (int) value;
                }
            }
        }
        return currPic;
    }

    public int getHopTimes(int n) {
        int count = 0;
        String binary = Integer.toBinaryString(n);
        String k = "0";
        int len = Integer.toBinaryString(n).length();
        for (int i = 0; i < 8 - len; i++) {
            binary = k + binary;
        }

        for (int j = 0; j < 7; j++) {
            if (binary.charAt(j) != binary.charAt((j + 1) % 8)) {
                count++;
            }
        }
        return count;
    }

    // 圆形LBP 等价模式
    public void setLBP() {

        //实现UniformPattern
        int temp=1;
        int table[]=new int[256];
        for(int m=0;m<256;m++){
            if (getHopTimes(m)<3){
                table[m]=temp;
                temp++;
            }
        }

        Picture currPic = this;
        int width = currPic.width;
        int height = currPic.height;

        //neighbors radius的值可以变化
        int neighbors = 8;
        double neighborhood[] = new double[8];
        int radius = 1;
        currPic.Lbpgray = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i >= 0 && i < radius) {
                    currPic.Lbpgray[i][j] = 0;
                    continue;
                }
                if (i >= (width - radius) && i < width) {
                    currPic.Lbpgray[i][j] = 0;
                    continue;
                }
                if (j >= 0 && j < radius) {
                    currPic.Lbpgray[i][j] = 0;
                    continue;
                }
                if (j >= (height - radius) && j < height) {
                    currPic.Lbpgray[i][j] = 0;
                    continue;
                }
                for (int k = 0; k < neighbors; k++) {
                    //计算采样点对于中心点坐标的偏移量rx，ry
                    double rx = radius * Math.cos(2.0 * Math.PI * k / neighbors);
                    double ry = -radius * Math.sin(2.0 * Math.PI * k / neighbors);
                    //为双线性插值做准备
                    //对采样点偏移量分别进行上下取整
                    int x1 = (int) Math.floor(rx);
                    int x2 = (int) Math.ceil(rx);
                    int y1 = (int) Math.floor(ry);
                    int y2 = (int) Math.ceil(ry);

                    //将坐标偏移量映射到0-1之间
                    double tx = rx - x1;
                    double ty = ry - y1;
                    //根据0-1之间的x，y的权重计算公式计算权重，权重与坐标具体位置无关，与坐标间的差值有关
                    double w1 = (1 - tx) * (1 - ty);
                    double w2 = tx * (1 - ty);
                    double w3 = (1 - tx) * ty;
                    double w4 = tx * ty;
                    neighborhood[k] = currPic.gray[i + x1][j + y1] * w1 + currPic.gray[i + x1][j + y2] * w2
                            + currPic.gray[i + x2][j + y1] * w3 + currPic.gray[i + x2][j + y2] * w4;
                }
                List<Integer> binaryList = new ArrayList<>();
                for (int n = 0; n < 8; n++) {
                    binaryList.add(judge((int) neighborhood[n], currPic.gray[i][j]));
                }

                double value = 0.0;
                for (int n = 0; n < 8; n++) {
                    value += binaryList.get(n) * Math.pow(2, 7 - n);
                }
                //等价模式
                currPic.Lbpgray[i][j] =table[(int) value] ;
            }
        }


    }

    //计算直方图 返回降维直方图
    public List<Double> getHistogram() {
        // int width = (int) Math.pow(2, neighbors);
        //直方图的宽高
        int width=59;
        int height = gradx * grady;
        //格子的宽高
        int w_grad = this.width / gradx;
        int h_grad = this.height / grady;
        //初始化
        this.histogram = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.histogram[i][j] = 0;
            }
        }
        //为直方图矩阵赋值
        for (int i = 0; i < gradx; i++) {
            for (int j = 0; j < grady; j++) {
                for (int m = i * w_grad; m < (i * w_grad + w_grad); m++) {
                    for (int n = j * h_grad; n < (j * h_grad + h_grad); n++) {
                        int x = i * gradx + j;//59种类别的一种
                        int y = this.Lbpgray[m][n];//
                        this.histogram[x][y] = this.histogram[x][y] + 1;
                    }
                }
            }
        }
        // return hisogram;
        // 直方图归一化 并降维
        this.histograms = new ArrayList<>();
        double values;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                values = this.histogram[i][j]*1.0 / (w_grad * h_grad);
                this.histograms.add(values);
            }
        }

        return this.histograms;
    }

    public double getHistogramsDistance(Picture target){
        List<Double>tar=target.histograms;
        List<Double>sou=this.histograms;
        double distance=0.0;
        double values=0.0;
        for(int i=0;i<59*gradx*grady;i++){
            //values=(tar.get(i)-sou.get(i))*(tar.get(i)-sou.get(i))/tar.get(i);
            if(tar.get(i)!=0){
                distance+=(tar.get(i)-sou.get(i))*(tar.get(i)-sou.get(i))/tar.get(i);
            }

        }
        return distance;
    }
}

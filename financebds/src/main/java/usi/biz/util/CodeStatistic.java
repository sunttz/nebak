package usi.biz.util;

import java.io.*;

public class CodeStatistic {
    public static int[] statistics(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            int[] sum = new int[]{0, 0, 0, 0, 0, 0};
            for (int i = 0; i < files.length; i++) {
                File fl = files[i];
                sum = plus(sum, statistics(fl));
            }
            return sum;
        } else {
            return read(file);
        }

    }

    private static int[] plus(int[] xx, int[] yy) {
        if (xx.length != yy.length) {
            throw new RuntimeException("异常");
        }
        int[] last = new int[xx.length];
        for (int i = 0; i < yy.length; i++) {
            last[i] = xx[i] + yy[i];
        }
        return last;
    }

    public static int[] read(File file) {
        String name = file.getName();
        int[] last = new int[]{0, 0, 0, 0, 0, 0};
        int sum1 = 0;
        int sum2 = 0;
        int sum3 = 0;
        if (name.endsWith(".java")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (!"".equals(line.trim())) {
                        if (!line.trim().startsWith("//")) {
                            sum1++;
                        }
                        sum2++;
                    }
                    sum3++;
                }
                last[0] = sum1;
                last[1] = sum2;
                last[2] = sum3;
                last[5] = 1;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            last[0] = 0;
            last[1] = 0;
            last[2] = 0;
            last[3] = 0;
            last[4] = 0;
            last[5] = 0;
        }
        return last;
    }

    /**
     * 功能:统计代码的行数
     * 说明:支持 整个工程的代码行数统计
     *
     * @param args
     */
    public static void main(String[] args) {
        String fileName = "/Users/taotaosun/Documents/snc_data/code/nebak/usi-bds-core/src/main";
        File file = new File(fileName);
        int[] sum = CodeStatistic.statistics(file);
        System.out.println("共有 " + sum[5] + " 个类");
        System.out.println("忽略注释忽略空行共 " + sum[0] + " 行");
        System.out.println("包括注释忽略空行共 " + sum[1] + " 行");
        System.out.println("包括注释包括空行共 " + sum[2] + " 行");


    }
}

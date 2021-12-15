import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

class DataUtil {

    private ArrayList<ArrayList<Double>> alllist = new ArrayList<ArrayList<Double>>(); // 存放所有数据
    private ArrayList<String> outlist = new ArrayList<String>(); // 存放输出数据，索引对应每个everylist的输出
    private ArrayList<String> checklist = new ArrayList<String>();  //存放测试集的真实输出字符串
    private int in_num = 0;
    private int out_num = 0; // 输入输出数据的个数
    private int type_num = 0; // 输出的类型数量
    private double[][] nom_data; //归一化输入数据中的最大值和最小值
    private int in_data_num = 0; //提前获得输入数据的个数

    // 获取输出类型的个数
    public int GetTypeNum() {
        return type_num;
    }

    // 设置输出类型的个数
    public void SetTypeNum(int type_num) {
        this.type_num = type_num;
    }

    // 获取输入数据的个数
    public int GetInNum() {
        return in_num;
    }

    // 获取输出数据的个数
    public int GetOutNum() {
        return out_num;
    }

    // 获取所有数据的数组
    public ArrayList<ArrayList<Double>> GetList() {
        return alllist;
    }

    // 获取输出为字符串形式的数据
    public ArrayList<String> GetOutList() {
        return outlist;
    }

    // 获取输出为字符串形式的数据
    public ArrayList<String> GetCheckList() {
        return checklist;
    }

    //返回归一化数据所需最大最小值
    public double[][] GetMaxMin(){

        return nom_data;
    }

    // 读取文件初始化数据
    public void ReadFile(String filepath, String sep, int flag)
            throws Exception {

        ArrayList<Double> everylist = new ArrayList<Double>(); // 存放每一组输入输出数据
        int readflag = flag; // flag=0,train;flag=1,test
        String encoding = "GBK";
        File file = new File(filepath);
        if (file.isFile() && file.exists()) { // 判断文件是否存在
            InputStreamReader read = new InputStreamReader(new FileInputStream(
                    file), encoding);// 考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            int aa = 0;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                int in_number = 0;
                System.out.println(aa++);
                String splits[] = lineTxt.split(sep); // 按','截取字符串
                if (readflag == 0) {

                    for (int i = 0; i < splits.length; i++)
                        try {
                            everylist.add(Normalize(Double.valueOf(splits[i]),nom_data[i][0],nom_data[i][1]));
                            in_number++;
                        } catch (Exception e) {
                            if (!outlist.contains(splits[i]))
                                outlist.add(splits[i]); // 存放字符串形式的输出数据

                            for (int k = 0; k < type_num; k++) {
                                everylist.add(0.0);
                            }
                            System.out.println(everylist);

                            everylist.set(in_number + outlist.indexOf(splits[i]), 1.0);
                        }
                } else if (readflag == 1) {
                    for (int i = 0; i < splits.length; i++)
                        try {
                            everylist.add(Normalize(Double.valueOf(splits[i]),nom_data[i][0],nom_data[i][1]));
                            in_number++;
                        } catch (Exception e) {
                            checklist.add(splits[i]); // 存放字符串形式的输出数据
                        }
                }
                alllist.add(everylist); // 存放所有数据
                in_num = in_number;
                out_num = type_num;
                everylist = new ArrayList<Double>();
                everylist.clear();

            }
            bufferedReader.close();
        }
    }

    //向文件写入分类结果
    public void WriteFile(String filepath, ArrayList<ArrayList<Double>> list, int in_number,  ArrayList<String> resultlist) throws IOException{
        File file = new File(filepath);
        FileWriter fw = null;
        BufferedWriter writer = null;
        try {
            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
            for(int i=0;i<list.size();i++){
                for(int j=0;j<in_number;j++)
                    writer.write(list.get(i).get(j)+",");
                writer.write(resultlist.get(i));
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            writer.close();
            fw.close();
        }
    }


    //学习样本归一化,找到输入样本数据的最大值和最小值
    public void NormalizeData(String filepath) throws IOException{
        //提前获得输入数据的个数
        GetBeforIn(filepath);
        int flag=1;
        nom_data = new double[in_data_num][2];
        String encoding = "GBK";
        File file = new File(filepath);
        if (file.isFile() && file.exists()) { // 判断文件是否存在
            InputStreamReader read = new InputStreamReader(new FileInputStream(
                    file), encoding);// 考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                String splits[] = lineTxt.split(","); // 按','截取字符串
                for (int i = 0; i < splits.length-1; i++){
                    if(flag==1){
                        nom_data[i][0]=Double.valueOf(splits[i]);
                        nom_data[i][1]=Double.valueOf(splits[i]);
                    }
                    else{
                        if(Double.valueOf(splits[i])>nom_data[i][0])
                            nom_data[i][0]=Double.valueOf(splits[i]);
                        if(Double.valueOf(splits[i])<nom_data[i][1])
                            nom_data[i][1]=Double.valueOf(splits[i]);
                    }
                }
                flag=0;
            }
            bufferedReader.close();
        }
    }

    //归一化前获得输入数据的个数
    public void GetBeforIn(String filepath) throws IOException{
        String encoding = "GBK";
        File file = new File(filepath);
        if (file.isFile() && file.exists()) { // 判断文件是否存在
            InputStreamReader read = new InputStreamReader(new FileInputStream(
                    file), encoding);// 考虑到编码格式
            //提前获得输入数据的个数
            BufferedReader beforeReader = new BufferedReader(read);
            String beforetext = beforeReader.readLine();
            String splits[] = beforetext.split(",");
            in_data_num = splits.length-1;
            beforeReader.close();
        }
    }

    //归一化公式
    public double Normalize(double x, double max, double min){
        double y = 0.1+0.8*(x-min)/(max-min);
        return y;
    }
}

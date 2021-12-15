import java.util.ArrayList;

public class Test {
    public static void main(String args[]) throws Exception {

        ArrayList<ArrayList<Double>> alllist = new ArrayList<ArrayList<Double>>(); // 存放所有数据
        ArrayList<String> outlist = new ArrayList<String>(); // 存放分类的字符串
        int in_num = 0, out_num = 0; // 输入输出数据的个数

        DataUtil dataUtil = new DataUtil(); // 初始化数据

        dataUtil.NormalizeData("D:\\project\\untitled\\src\\train.txt");

        dataUtil.SetTypeNum(3); // 设置输出类型的数量
        dataUtil.ReadFile("D:\\project\\untitled\\src\\train.txt", ",", 0);

        in_num = dataUtil.GetInNum(); // 获得输入数据的个数
        out_num = dataUtil.GetOutNum(); // 获得输出数据的个数(个数代表类型个数)
        alllist = dataUtil.GetList(); // 获得初始化后的数据

        outlist = dataUtil.GetOutList();
        System.out.print("分类的类型：");
        for(int i =0 ;i<outlist.size();i++)
            System.out.print(outlist.get(i)+"  ");
        System.out.println();
        System.out.println("训练集的数量："+alllist.size());

        BPNN bpnn = new BPNN();
        // 训练
        System.out.println("Train Start!");
        System.out.println(".............");
        bpnn.Train(in_num, out_num, alllist);
        System.out.println("Train End!");

        // 测试
        DataUtil testUtil = new DataUtil();

        testUtil.NormalizeData("D:\\project\\untitled\\src\\test.txt");

        testUtil.SetTypeNum(3); // 设置输出类型的数量
        testUtil.ReadFile("D:\\project\\untitled\\src\\test.txt", ",", 1);

        ArrayList<ArrayList<Double>> testList = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> resultList = new ArrayList<ArrayList<Double>>();
        ArrayList<String> normallist = new ArrayList<String>(); // 存放测试集标准的输出字符串
        ArrayList<String> resultlist = new ArrayList<String>(); // 存放测试集计算后的输出字符串

        double right = 0; // 分类正确的数量
        int type_num = 0; // 类型的数量
        double all_num = 0; //测试集的数量
        type_num = outlist.size();

        testList = testUtil.GetList(); // 获取测试数据
        normallist = testUtil.GetCheckList();

        int errorcount=0; // 分类错误的数量
        resultList = bpnn.ForeCast(testList); // 测试
        all_num=resultList.size();
        for (int i = 0; i < resultList.size(); i++) {
            String checkString = "unknow";
            for (int j = 0; j < type_num; j++) {
                if(resultList.get(i).get(j)==1.0){
                    checkString = outlist.get(j);
                    resultlist.add(checkString);
                }
                /*else{
                    resultlist.add(checkString);
                }*/
            }
            /*
            if(checkString.equals("unknow"))
                errorcount++;
            */
            if(checkString.equals(normallist.get(i)))
                right++;
        }
        testUtil.WriteFile("D:\\project\\untitled\\src\\tet.txt",testList,in_num,resultlist);

        System.out.println("测试集的数量："+ (new Double(all_num)).intValue());
        System.out.println("分类正确的数量："+(new Double(right)).intValue());
        System.out.println("算法的分类正确率为："+right/all_num);

        System.out.println("分类结果存储在：D:\\project\\untitled\\src\\tet.txt");
    }
}
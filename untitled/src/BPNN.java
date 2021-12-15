import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class BPNN {
    // private static int LAYER = 3; // 三层神经网络
    private static int NodeNum = 10; // 每层的最多节点数
    private static final int ADJUST = 5; // 隐层节点数调节常数
    private static final int MaxTrain = 2000; // 最大训练次数
    private static final double ACCU = 0.015; // 每次迭代允许的误差 iris:0.015
    private double ETA_W = 0.5; // 权值学习效率0.5
    private double ETA_T = 0.5; // 阈值学习效率
    private double accu;

    // 附加动量项
    //private static final double ETA_A = 0.3; // 动量常数0.1
    //private double[][] in_hd_last; // 上一次的权值调整量
    //private double[][] hd_out_last;

    private int in_num; // 输入层节点数
    private int hd_num; // 隐层节点数
    private int out_num; // 输入出节点数

    private ArrayList<ArrayList<Double>> list = new ArrayList<>(); // 输入输出数据

    private double[][] in_hd_weight; // BP网络in-hidden突触权值
    private double[][] hd_out_weight; // BP网络hidden_out突触权值
    private double[] in_hd_th; // BP网络in-hidden阈值
    private double[] hd_out_th; // BP网络hidden-out阈值

    private double[][] out; // 每个神经元的值经S型函数转化后的输出值，输入层就为原值
    private double[][] delta; // delta学习规则中的值

    // 获得网络三层中神经元最多的数量
    public int GetMaxNum() {
        return Math.max(Math.max(in_num, hd_num), out_num);
    }

    // 设置权值学习率
    public void SetEtaW() {
        ETA_W = 0.5;
    }

    // 设置阈值学习率
    public void SetEtaT() {
        ETA_T = 0.5;
    }

    // BPNN训练
    public void Train(int in_number, int out_number,
                      ArrayList<ArrayList<Double>> arraylist) throws IOException {
        list = arraylist;
        in_num = in_number;
        out_num = out_number;

        GetNums(in_num, out_num); // 获取输入层、隐层、输出层的节点数
        // SetEtaW(); // 设置学习率
        // SetEtaT();

        InitNetWork(); // 初始化网络的权值和阈值

        int datanum = list.size(); // 训练数据的组数
        int createsize = GetMaxNum(); // 比较创建存储每一层输出数据的数组
        out = new double[3][createsize];

        for (int iter = 0; iter < MaxTrain; iter++) {
            for (int cnd = 0; cnd < datanum; cnd++) {
                // 第一层输入节点赋值

                for (int i = 0; i < in_num; i++) {
                    out[0][i] = list.get(cnd).get(i); // 为输入层节点赋值，其输入与输出相同
                }
                Forward(); // 前向传播
                Backward(cnd); // 误差反向传播

            }
            System.out.println("This is the " + (iter + 1)
                    + " th trainning NetWork !");
            accu = GetAccu();
            System.out.println("All Samples Accuracy is " + accu);
            if (accu < ACCU)
                break;

        }

    }

    // 获取输入层、隐层、输出层的节点数，in_number、out_number分别为输入层节点数和输出层节点数
    public void GetNums(int in_number, int out_number) {
        in_num = in_number;
        out_num = out_number;
        hd_num = (int) Math.sqrt(in_num + out_num) + ADJUST;
        if (hd_num > NodeNum)
            hd_num = NodeNum; // 隐层节点数不能大于最大节点数
    }

    // 初始化网络的权值和阈值
    public void InitNetWork() {
        // 初始化上一次权值量,范围为-0.5-0.5之间
        //in_hd_last = new double[in_num][hd_num];
        //hd_out_last = new double[hd_num][out_num];

        in_hd_weight = new double[in_num][hd_num];
        for (int i = 0; i < in_num; i++)
            for (int j = 0; j < hd_num; j++) {
                int flag = 1; // 符号标志位(-1或者1)
                if ((new Random().nextInt(2)) == 1)
                    flag = 1;
                else
                    flag = -1;
                in_hd_weight[i][j] = (new Random().nextDouble() / 2) * flag; // 初始化in-hidden的权值
                //in_hd_last[i][j] = 0;
            }

        hd_out_weight = new double[hd_num][out_num];
        for (int i = 0; i < hd_num; i++)
            for (int j = 0; j < out_num; j++) {
                int flag = 1; // 符号标志位(-1或者1)
                if ((new Random().nextInt(2)) == 1)
                    flag = 1;
                else
                    flag = -1;
                hd_out_weight[i][j] = (new Random().nextDouble() / 2) * flag; // 初始化hidden-out的权值
                //hd_out_last[i][j] = 0;
            }

        // 阈值均初始化为0
        in_hd_th = new double[hd_num];
        for (int k = 0; k < hd_num; k++)
            in_hd_th[k] = 0;

        hd_out_th = new double[out_num];
        for (int k = 0; k < out_num; k++)
            hd_out_th[k] = 0;

    }

    // 计算单个样本的误差
    public double GetError(int cnd) {
        double ans = 0;
        for (int i = 0; i < out_num; i++)
            ans += 0.5 * (out[2][i] - list.get(cnd).get(in_num + i))
                    * (out[2][i] - list.get(cnd).get(in_num + i));
        return ans;
    }

    // 计算所有样本的平均精度
    public double GetAccu() {
        double ans = 0;
        int num = list.size();
        for (int i = 0; i < num; i++) {
            int m = in_num;
            for (int j = 0; j < m; j++)
                out[0][j] = list.get(i).get(j);
            Forward();
            int n = out_num;
            for (int k = 0; k < n; k++)
                ans += 0.5 * (list.get(i).get(in_num + k) - out[2][k])
                        * (list.get(i).get(in_num + k) - out[2][k]);
        }
        return ans / num;
    }

    // 前向传播
    public void Forward() {
        // 计算隐层节点的输出值
        for (int j = 0; j < hd_num; j++) {
            double v = 0;
            for (int i = 0; i < in_num; i++)
                v += in_hd_weight[i][j] * out[0][i];
            v += in_hd_th[j];
            out[1][j] = Sigmoid(v);
        }
        // 计算输出层节点的输出值
        for (int j = 0; j < out_num; j++) {
            double v = 0;
            for (int i = 0; i < hd_num; i++)
                v += hd_out_weight[i][j] * out[1][i];
            v += hd_out_th[j];
            out[2][j] = Sigmoid(v);
        }
    }

    // 误差反向传播
    public void Backward(int cnd) {
        CalcDelta(cnd); // 计算权值调整量
        UpdateNetWork(); // 更新BP神经网络的权值和阈值
    }

    // 计算delta调整量
    public void CalcDelta(int cnd) {

        int createsize = GetMaxNum(); // 比较创建数组
        delta = new double[3][createsize];
        // 计算输出层的delta值
        for (int i = 0; i < out_num; i++) {
            delta[2][i] = (list.get(cnd).get(in_num + i) - out[2][i])
                    * SigmoidDerivative(out[2][i]);
        }

        // 计算隐层的delta值
        for (int i = 0; i < hd_num; i++) {
            double t = 0;
            for (int j = 0; j < out_num; j++)
                t += hd_out_weight[i][j] * delta[2][j];
            delta[1][i] = t * SigmoidDerivative(out[1][i]);
        }
    }

    // 更新BP神经网络的权值和阈值
    public void UpdateNetWork() {

        // 隐含层和输出层之间权值和阀值调整
        for (int i = 0; i < hd_num; i++) {
            for (int j = 0; j < out_num; j++) {
                hd_out_weight[i][j] += ETA_W * delta[2][j] * out[1][i]; // 未加权值动量项
                /* 动量项
                 * hd_out_weight[i][j] += (ETA_A * hd_out_last[i][j] + ETA_W
                 * delta[2][j] * out[1][i]); hd_out_last[i][j] = ETA_A *
                 * hd_out_last[i][j] + ETA_W delta[2][j] * out[1][i];
                 */
            }

        }
        for (int i = 0; i < out_num; i++)
            hd_out_th[i] += ETA_T * delta[2][i];

        // 输入层和隐含层之间权值和阀值调整
        for (int i = 0; i < in_num; i++) {
            for (int j = 0; j < hd_num; j++) {
                in_hd_weight[i][j] += ETA_W * delta[1][j] * out[0][i]; // 未加权值动量项
                /* 动量项
                 * in_hd_weight[i][j] += (ETA_A * in_hd_last[i][j] + ETA_W
                 * delta[1][j] * out[0][i]); in_hd_last[i][j] = ETA_A *
                 * in_hd_last[i][j] + ETA_W delta[1][j] * out[0][i];
                 */
            }
        }
        for (int i = 0; i < hd_num; i++)
            in_hd_th[i] += ETA_T * delta[1][i];
    }

    // 符号函数sign
    public int Sign(double x) {
        if (x > 0)
            return 1;
        else if (x < 0)
            return -1;
        else
            return 0;
    }

    // 返回最大值
    public double Maximum(double x, double y) {
        if (x >= y)
            return x;
        else
            return y;
    }

    // 返回最小值
    public double Minimum(double x, double y) {
        if (x <= y)
            return x;
        else
            return y;
    }

    // log-sigmoid函数
    public double Sigmoid(double x) {
        return (double) (1 / (1 + Math.exp(-x)));
    }

    // log-sigmoid函数的倒数
    public double SigmoidDerivative(double y) {
        return (double) (y * (1 - y));
    }

    // tan-sigmoid函数
    public double TSigmoid(double x) {
        return (double) ((1 - Math.exp(-x)) / (1 + Math.exp(-x)));
    }

    // tan-sigmoid函数的倒数
    public double TSigmoidDerivative(double y) {
        return (double) (1 - (y * y));
    }

    // 分类预测函数
    public ArrayList<ArrayList<Double>> ForeCast(
            ArrayList<ArrayList<Double>> arraylist) {

        ArrayList<ArrayList<Double>> alloutlist = new ArrayList<>();
        ArrayList<Double> outlist = new ArrayList<Double>();
        int datanum = arraylist.size();
        for (int cnd = 0; cnd < datanum; cnd++) {
            for (int i = 0; i < in_num; i++)
                out[0][i] = arraylist.get(cnd).get(i); // 为输入节点赋值
            Forward();
            for (int i = 0; i < out_num; i++) {
                if (out[2][i] > 0 && out[2][i] < 0.5)
                    out[2][i] = 0;
                else if (out[2][i] > 0.5 && out[2][i] < 1) {
                    out[2][i] = 1;
                }
                outlist.add(out[2][i]);
            }
            alloutlist.add(outlist);
            outlist = new ArrayList<Double>();
            outlist.clear();
        }
        return alloutlist;
    }

}

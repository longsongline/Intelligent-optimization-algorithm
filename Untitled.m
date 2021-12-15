function [ Result ] = Untitled( Data,k )
%计算K近邻的一个通用函数
%要求一行为一个多维变量
%返回Result为一个矩阵，第i行为到第i个数据点的欧几里得距离最短的k个数据点序号的排列，列序数越小距离越近
%计算KNN用KD树要更好一点，这个第一版暂时先用普通方法了

datasize = size(Data);
Distance = zeros(datasize(1),datasize(1));%距离矩阵，存放两点之间的距离

%%%%
 % 计算距离矩阵
 %
%%%%
for i = 1:datasize(1)-1    
    for j = i+1:datasize(1)
        tempsum = 0;
        for ci = 1:datasize(2)
            tempsum = tempsum + (Data(i,ci)-Data(j,ci))*(Data(i,ci)-Data(j,ci));
        end
        Distance(i,j) = sqrt(tempsum);
        Distance(j,i) = sqrt(tempsum);
    end    
end

Result = zeros(datasize(1),k);%结果矩阵
%%%%
 % 计算结果矩阵
 %
%%%%
for i = 1:datasize(1)
    
    for j = 1:datasize(1)
        
        if i==j
            continue;
        end
        
        index = k;
        while index>0 && Result(i,index)==0
            index = index-1;
        end       
        if index == 0
            Result(i,1) = j;
        else
            if Distance(i,j)<Distance(i,Result(i,index))
                while index>0 && Distance(i,j)<Distance(i,Result(i,index))
                    temp = Result(i,index);
                    Result(i,index) = j;
                    if index<k
                        Result(i,index+1) = temp;
                    end
                    index = index-1;
                end
            else
                if index<k
                    Result(i,index+1) = j;
                end
            end
        end
             
    end
        
end



end

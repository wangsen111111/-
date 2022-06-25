package cn.hy.utils;

public class CharArrayParser {


    /**
     * 给一个 char数组，和一个 char，要求删除这个Char数组里面的所有匹配的char，返回处理后的数组。
     * 不匹配的字符需要前移，数组尾部空出的位置以 '-' 填充。
     * 请尽量以算法复杂度 O(n) 实现优化算法。
     * 注意：不使用String.replace等系统函数，不使用新的List, StringBuffer等数据结构，不生成新的数组。
     * <p>
     * 比如，
     * ['a', 'b', 'c', 'd', 'a', 'd']，'a'，处理后返回数组['b', 'c', 'd', 'd', '-', '-']
     *
     * @param charArr      需要查找的字符数组
     * @param charToRemove 需要删除的字符
     * @return 删除成功返回对应字符数组
     */
    public static char[] removeChar(char[] charArr, char charToRemove) {
        // 双指针的方法
        int slow = 0;   //代表需要替换的位置
        int fast = 0;   //代表前进的指针
        boolean flag = false;   //是否需要替换
        while(fast<charArr.length){
            if(charArr[fast]==charToRemove){
                charArr[fast] = '-';
                if (flag) {

                }else{
                    slow = fast;
                }
                fast++;
                flag = true;
            }else{
                if(flag){
                    charArr[slow] = charArr[fast];
                    charArr[fast] = '-';
                    if(slow+1<charArr.length&&charArr[slow+1]=='-'){
                        slow+=1;
                    }else {
                        slow = fast;
                    }
                }
                fast++;
            }
        }
        return charArr;
    }

    public static void main(String[] args) {
//        char[] chars = {'a', 'b', 'c', 'd', 'a', 'd'};
        char[] chars = {'b', 'a', 'c', 'a', 'a', 'd', 'e'};
        System.out.println("begin ...");
        char[] s = removeChar(chars, 'a');

        for (char c : s) {
            System.out.println(c);
        }

        System.out.println("end ...");

    }
}
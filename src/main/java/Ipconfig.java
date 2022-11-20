
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author
 * @date
 * @version
 * java代码 调用dos的ipconfig /all 命令，获取网卡详细信息
 */

public class Ipconfig {
    public static void main(String[] args)
    {
        Process p;
        //此处输入要执行的cmd命令
        String cmd="ping baidu.com";
        try
        {
            //exec执行cmd命令
            p = Runtime.getRuntime().exec(cmd);
            //获取CMD命令结果的输出流
            InputStream fis=p.getInputStream();
            //用一个读输出流类去读，"Shift_JIS"是字符集
            InputStreamReader isr=new InputStreamReader(fis,"gbk");
            //使用缓冲器读取ZZ
            BufferedReader br=new BufferedReader(isr);
            String line=null;
            //全部读取完成为止，一行一行读取
            while((line=br.readLine())!=null)
            {
                //输出读取的内容
                System.out.println( line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}

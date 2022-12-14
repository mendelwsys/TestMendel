package su.darktime;


import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class ScriptProcessor
{
    public static List<String> getSqlArray(String filePath) throws Exception
    {
        List<String> cmds = new LinkedList<>();
        BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        StringBuilder bld = new StringBuilder();
        String ls;
        while ((ls=sr.readLine())!=null)
        {
//            ls = ls.trim();
//            if (ls.endsWith(";"))
            int ix=ls.indexOf(";");
            if (ix>=0 && ls.substring(ix).trim().length()==1)
            {
                ls=ls.substring(0,ls.length()-1);
                bld=bld.append(ls);
                cmds.add(bld.toString());
                bld = new StringBuilder();
            }
            else
                bld.append(ls);
        }
        return cmds;
    }
}

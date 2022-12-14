package su.darktime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrderGenerator
{
    public static final long SDEF_TART_ORDER_NUM = 43351231534l;
    private long startNumber = SDEF_TART_ORDER_NUM;
    private int daysInterval = 50;
    private static final String[] statusPos  = {"VRF","T1","T2","T3","DSC"};
    private int guidIdStart = 1234;

    public static final  String [][] produtSubProduct = {
                                    {"24_O15_0001","O15"},{"24_O16_0001","O16"},
                                    {"24_O17_0001","O17"},{"24_O18_0001","O18"},
                                    {"24_O19_0001","O19"},{"24_O20_0001","O20"},
                                    {"24_O21_0001","O21"},{"24_O22_0001","O22"},
                                    {"24_O23_0001","O23"},{"24_O24_0001","O24"},
                                    {"24_O25_0001","O25"},{"24_O26_0001","O26"},
                                    {"24_O27_0001","O27"},{"24_O28_0001","O28"}
    };

    final static String tT_START = "INSERT INTO T_START " +
            "( time_stamp,app_num,product,subproduct) VALUES " +
            "( TIMESTAMP(:CURRENT_TIMESTAMP),:NUM_O,:PRODUCT,:SUBPRODUCT);";
    final static String tT_BEGIN=
            "INSERT INTO T_BEGIN ( time_stamp,app_num,subproduct,step_id,step_guid) VALUES " +
                    "( TIMESTAMP(:CURRENT_TIMESTAMP),:NUM_O,:SUBPRODUCT,:STEP_ID,:STEP_GUID);";
    final static String tT_END ="INSERT INTO T_END ( time_stamp,step_guid,step_result_num,step_result) VALUES " +
            "( TIMESTAMP(:CURRENT_TIMESTAMP),:STEP_GUID,0,'OK');";

    static final public Random rnd = new Random(1733);
    public List<String> generateOne(boolean fullPath,boolean noLast)
    {
        List<String> rv=new LinkedList<>();
        daysInterval = (int) Math.round((rnd.nextDouble()*daysInterval)+10);
        LocalDateTime stateDate = LocalDateTime.now();
        stateDate=stateDate.minusDays(daysInterval);

        List<String> replaceByIt = new LinkedList<String>();
        String lCurrent_TIMESTAMP = getDateByInterval(stateDate);
        replaceByIt.add(lCurrent_TIMESTAMP);
        String l_NAMO = wrapQT(Long.toString(startNumber));
        replaceByIt.add(l_NAMO);
        int cntP = (int) Math.round (rnd.nextDouble()*(produtSubProduct.length-1));
        String l_PRODUCT = wrapQT(produtSubProduct[cntP][0]);
        replaceByIt.add(l_PRODUCT);
        String l_SUBPRODUCT = wrapQT(produtSubProduct[cntP][1]);
        replaceByIt.add(l_SUBPRODUCT);
        rv.add(DbUtils.replacePlaceHolders(tT_START,replaceByIt));
        int maxPos= fullPath?statusPos.length:(int) Math.round(rnd.nextDouble()*(statusPos.length-1));
        for (int posI=0;posI<maxPos;posI++)
        {
            replaceByIt = new LinkedList<String>();
            int cntDay = (int) Math.round(rnd.nextDouble()*daysInterval/2 );
            daysInterval-=cntDay;

            stateDate=cntDay>1?stateDate.plusDays(cntDay):stateDate.plusHours(2);

            lCurrent_TIMESTAMP = getDateByInterval(stateDate);
            replaceByIt.add(lCurrent_TIMESTAMP);
            replaceByIt.add(l_NAMO);
            replaceByIt.add(l_SUBPRODUCT);
            replaceByIt.add(wrapQT(statusPos[posI]));
            String l_STEP_GUID = wrapQT(Integer.toString(guidIdStart));
            replaceByIt.add(l_STEP_GUID);
            //"( TIMESTAMP(:CURRENT_TIMESTAMP),:NUM_O,:SUBPRODUCT,:STEP_ID,:STEP_GUID);";
            String sqlBegin = DbUtils.replacePlaceHolders(tT_BEGIN, replaceByIt);
            rv.add(sqlBegin);
            if ( (posI<maxPos-1 && (daysInterval>0 || fullPath)) || (fullPath && !noLast) )
            {
                replaceByIt = new LinkedList<String>();
                cntDay = (int) Math.round(rnd.nextDouble()*daysInterval/2);
                daysInterval-=cntDay;

                stateDate=cntDay>1?stateDate.plusDays(cntDay):stateDate.plusHours(1);

                lCurrent_TIMESTAMP = getDateByInterval(stateDate);
                replaceByIt.add(lCurrent_TIMESTAMP);
                replaceByIt.add(l_STEP_GUID);
                //( TIMESTAMP(:CURRENT_TIMESTAMP),:STEP_GUID,0,'OK')
                String sqlEnd = DbUtils.replacePlaceHolders(tT_END, replaceByIt);
                rv.add(sqlEnd);
            }
            guidIdStart++;
            if (daysInterval<=1 && !fullPath)
                break;
        }
        startNumber++;
        return rv;
    }


    private String wrapQT(String s) {
        return DbUtils.wrapQT(s);
    }

    public String getDateByInterval(LocalDateTime date) {
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("''yyyy-MM-dd'',''HH:mm:ss''");//.withZone(ZoneId.systemDefault());
        return date.format(formatterDate);
    }

    public List<String> generate(int cnt)
    {
        List<String> rv = new LinkedList<>();
        for (int i=0;i<cnt;i++)
            rv.addAll(this.generateOne(rnd.nextBoolean(),rnd.nextBoolean()));
        return rv;
    }

    public static void main(String[] args) {
        OrderGenerator orderGenerator = new OrderGenerator();
        List<String> ll = orderGenerator.generate(10);
            for (String s : ll) {
                System.out.println(":" + s);
            }
//            System.out.println("\n");

    }
}

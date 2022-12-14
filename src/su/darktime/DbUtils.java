package su.darktime;

import java.sql.*;
import java.util.*;

public class DbUtils
{
    public static void  execSQL(Connection conn,String sql,boolean keepConnAlive) throws Exception
    {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.execute();
            stmt.close();
        }
        finally
            {
                closeAll(rs, stmt, conn,keepConnAlive);
            }

    }

    public static String constructSQLRequest(String pSQL, Map<String, List<Integer>> name2pos)
    {
        if (pSQL!=null)
        {
            int ixSemi=0;
            int ix=1;
            while ((ixSemi=pSQL.indexOf(":",ixSemi))>=0)
            {
                int ixEnd=pSQL.indexOf(" ",ixSemi);
                if (ixEnd<0)
                    ixEnd=pSQL.length();
                String name=pSQL.substring(ixSemi,ixEnd);
                name = name.substring(1);

                List<Integer> positions = name2pos.get(name);
                if (positions==null)
                    name2pos.put(name, positions = new LinkedList<Integer>());
                positions.add(ix);

                pSQL=pSQL.substring(0,ixSemi)+" ? "+pSQL.substring(ixEnd);
            }
        }
        return pSQL;
    }

    public static String wrapQT(String s) {
        return "'"+ s +"'";
    }

    public static String replacePlaceHolders(String forReplace, Collection<String> replaceByIt)
    {
        String[] _names=replaceByIt.toArray(new String[replaceByIt.size()]);
        return replacePlaceHolders(forReplace,_names);
    }
    public static String replacePlaceHolders(String forReplace,String[] replaceByIt)
    {
        int ixFnd = 0;
        for (String name : replaceByIt)
        {
            int ixBegin = forReplace.indexOf(":", ixFnd);

//            int ixEnd = forReplace.indexOf(",", ixBegin);
            int ixEnd = getMinTermIx(forReplace, ixBegin);
            if (ixEnd<0)
                return forReplace;

            ixEnd = ixEnd > 0 ? Math.min(ixEnd, forReplace.indexOf(")", ixBegin)) : forReplace.indexOf(")", ixBegin);
            String substring = forReplace.substring(ixBegin, ixEnd);
            ixFnd = ixEnd - substring.length() + name.length();
            forReplace = forReplace.replace(substring, name);

        }
        return forReplace;
    }
    final static String endTerms[] = {" ",";",")","*",",","(","/","+","-"};

    public static String replacePlaceHolders(String forReplace,Map<String,String> holder2replaceByIt)
    {
        int ixFnd = 0;
        for (String holder : holder2replaceByIt.keySet())
        {
            String name = holder2replaceByIt.get(holder);
            while (true)
            {
                int ixBegin = forReplace.indexOf(":" + holder, ixFnd);
                if (ixBegin<0)
                    break;
                int ixEnd = getMinTermIx(forReplace, ixBegin);
                if (ixEnd<0)
                    return forReplace;

//                int ixEnd = forReplace.indexOf(",", ixBegin);
//                ixEnd = ixEnd > 0 ? Math.min(ixEnd, forReplace.indexOf(")", ixBegin)) : forReplace.indexOf(")", ixBegin);

                String substring = forReplace.substring(ixBegin, ixEnd);
                ixFnd = ixEnd - substring.length() + name.length();
                forReplace = forReplace.replace(substring, name);
            }
        }
        return forReplace;
    }

    private static int getMinTermIx(String forReplace, int ixBegin) {
        int ixEnd=-1;
        for (String endTerm : endTerms)
        {
            int _ixEnd = forReplace.indexOf(endTerm, ixBegin);
            ixEnd = ixEnd < 0 ? _ixEnd : (_ixEnd < 0 ?ixEnd:Math.min(_ixEnd, ixEnd)  );
        }
        return ixEnd;
    }


    public static Object[][] execSQL(Connection conn,String pSQL, List<ColumnHeadBean> listOfColumn,boolean keepConnAlive)  throws Exception
    {
        PreparedStatement stmt=null;
        ResultSet rs = null;
        try
        {
            stmt=conn.prepareStatement(pSQL);
            rs=stmt.executeQuery();

            if (listOfColumn!=null)
                setMetaData(rs,listOfColumn);


            List<Object[]> rv=new LinkedList<Object[]>();
            int cnt=rs.getMetaData().getColumnCount();
            if (cnt>0)
            {
                while (rs.next())
                {
                    Object[] objs=new Object[cnt];
                    for (int i=1;i<=cnt;i++)
                        objs[i-1]=rs.getObject(i);
                    rv.add(objs);
                }
            }
            return rv.toArray(new Object[rv.size()][]);
        }
        finally
        {
            closeAll(rs, stmt, conn,keepConnAlive);
        }
    }

    public static Object[][] execSQL(Connection conn,String pSQL, Map<String, Object> params, Map<String, List<Integer>> name2pos,List<ColumnHeadBean> listOfColumn)  throws Exception
    {
        PreparedStatement stmt=null;
        ResultSet rs = null;
        try
        {
            stmt=conn.prepareStatement(pSQL);
            for (String nameP : params.keySet())
            {
                List<Integer> lPos = name2pos.get(nameP);
                for (Integer lPo : lPos)
                    stmt.setObject(lPo,params.get(nameP));
            }

            rs=stmt.executeQuery();

            if (listOfColumn!=null)
                setMetaData(rs,listOfColumn);


            List<Object[]> rv=new LinkedList<Object[]>();
            int cnt=rs.getMetaData().getColumnCount();
            if (cnt>0)
            {
                while (rs.next())
                {
                    Object[] objs=new Object[cnt];
                    for (int i=1;i<=cnt;i++)
                        objs[i-1]=rs.getObject(i);
                    rv.add(objs);
                }
            }
            return rv.toArray(new Object[rv.size()][]);
        }
        finally
        {
            closeAll(rs, stmt, conn);
        }
    }

    public static Object[][] execQuery(Connection conn,String query,List<ColumnHeadBean> listOfColumn) throws Exception
    {
        Statement stmt=null;
        ResultSet rs = null;
        try
        {
            stmt=conn.createStatement();
            rs=stmt.executeQuery(query);

            if (listOfColumn!=null)
                setMetaData(rs,listOfColumn);

            List<Object[]> rv=new LinkedList<Object[]>();
            int cnt=rs.getMetaData().getColumnCount();
            if (cnt>0)
            {
                while (rs.next())
                {
                    Object[] objs=new Object[cnt];
                    for (int i=1;i<=cnt;i++)
                        objs[i-1]=rs.getObject(i);
                    rv.add(objs);
                }
            }
            return rv.toArray(new Object[rv.size()][]);
        }
        finally
        {
            closeAll(rs, stmt, conn);
        }
    }

    public static void loadDriver(String driver) throws Exception
    {
        Class.forName(driver).newInstance();
    }

    public static Connection getConnection(String url) throws ClassNotFoundException, SQLException
    {
        return DriverManager.getConnection(url);
    }

    public static void closeStatement(final Statement stmt) {
        try{
            if(stmt != null) stmt.close();
        }catch(SQLException e){
            // ignore
        }
    }
    //
    public static void closeResultSet(final ResultSet rs){
        try{
            if(rs != null) rs.close();
        }catch(SQLException e){
            // ignore
        }
    }

    public static void closeAll(final ResultSet rs, final Statement stmt, final Connection conn,boolean keepConnAlive){
        try{
            if(rs   != null) rs.close();
        }
        catch(SQLException e)
        {
            // ignore
        }
        try{
            if(stmt != null) stmt.close();
        }
        catch(SQLException e)
        {
            // ignore
        }

        try{
            if(conn != null && !keepConnAlive)
            {
                    conn.close();
            }
        }
        catch(SQLException e)
        {
            // ignore
        }

    }
    //
    public static void closeAll(final ResultSet rs, final Statement stmt, final Connection conn){
        closeAll(rs,stmt,conn,false);
    }

    public static void setMetaData(ResultSet rs,List<ColumnHeadBean> listOfColumn) throws Exception
    {
        ResultSetMetaData meta = rs.getMetaData();
        int colCnt = meta.getColumnCount();
        for (int i = 1; i <= colCnt; i++)
            listOfColumn.add(new ColumnHeadBean(meta.getColumnName(i),meta.getColumnName(i), meta.getColumnClassName(i)));
    }
}

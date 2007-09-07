package org.firebirdsql.javaudf;
import java.lang.reflect.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class GetJNIHelpers {
  Method mth;
  public String ClassConv(Class c)
  {
   String s="";
   if(c.isPrimitive())
   {
     if(c.isArray())
       s+='[';
     String s1=c.toString();
     if(s1.equals("void"))
       s += "V";
     else
     if(s1.equals("boolean"))
         s += "Z";
     else
     if(s1.equals("byte"))
         s+= "B";
     else
     if(s1.equals("char"))
         s+= "C";
     else
     if(s1.equals("short"))
         s+= "S";
     else
     if(s1.equals("int"))
         s+= "I";
     else
     if(s1.equals("long"))
         s+= "J";
     else
     if(s1.equals("float"))
         s+= "F";
     else
     if(s1.equals("double"))
         s+= "D";
   }
   else
   {
     String s1=c.toString();
     s1=s1.replace('.','/');
     if(c.isInterface())
       s1=s1.substring(10);
     else
       s1=s1.substring(6);
     if(!c.isArray())
       s1='L'+s1+';';
     s+=s1;
   }
   return s;
  }
  public GetJNIHelpers() {
  }
  public String getSignature()
  {
   String s="(";
   Class ss[]=mth.getParameterTypes();
   for(int i=0;i<ss.length;i++)
    s+=ClassConv(ss[i]);
   s+=')';
   s+=ClassConv(mth.getReturnType());
   return s;
  }
  public static String repl(String s)
  {
   String r="";
   for(int i=0;i<s.length();i++)
     if("_".equals(s.substring(i,i+1)))
       r+="_1";
     else
       r+=s.substring(i,i+1);
   return r;
  }
  public int[]t(){return null;}
  public native String[]s();
  public static void main(String[] args) {
    GetJNIHelpers a = new GetJNIHelpers();
//    Method m[]=org.firebirdsql.ngds.GDS_Impl.class.getDeclaredMethods();
//    Class c_=org.firebirdsql.ngds.GDS_Impl.class;
//    String cls=c_.getName().replace('.','/')+'/';
//    System.out.println(m.length);
//    for(int i=0;i<m.length;i++)
//    if((m[i].getModifiers()&Modifier.NATIVE)!=0)
//    {
//      a.mth = m[i];
//      String s=cls+m[i].getName(),s1;
//      s1=repl(s);
//      s1=s1.replace('/','_');
//      System.out.println("{\""+s+"\",");
//      System.out.println('"'+a.getSignature()+"\",");
//      System.out.println("&Java_"+s1+"},");
//    }
  }
}
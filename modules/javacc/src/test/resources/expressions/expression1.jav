import java.util.*;

List
{
    List list = new java.util.ArrayList();  
    int i = 1;
    do
    {
        if(i == 3)
        {
            i++;
            continue;            
        }
        String str = new String("\"str 'List");
        list.add(str+i);
        i++;
        
    }while (i < 5);
    return list;
}
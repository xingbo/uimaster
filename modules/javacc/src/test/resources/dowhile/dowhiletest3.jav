import java.util.*;
{
    List list = new ArrayList();
    int i = 1;
    do
    {
        if(i == 3)
        {
            i++;
            continue;            
        }
        String str = new String("str");
        list.add(str+i);
        i++;
        
    }while (i < 5);
    return list;
}
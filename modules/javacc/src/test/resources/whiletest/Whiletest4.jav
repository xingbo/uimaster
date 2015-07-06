{
    int i = 10;
    int k = 0;
    while(i > 0)
    {
        if(i == 8)
        {
            i--;
            continue;
        }
        i--;
        if(i == 2)
            break;
        int j = i;
        while(j % 17 != 0)
        {
            j = j + i;
            k = k + j;            
            if(k >= 200)
                break;
        }        
            
    }
    return k;
}
{
    int i = 1;
    int a = 0;
    while (i < 100)
    {
        if(i == 50)
        {
            i++;
            continue;
        }
        a=a+i;
        i++;
    }
    return a;
}
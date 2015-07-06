{
    int i = 1;
    int a = i;
    do
    {
        a++;
        i++;
        if(i == 3)
            break;
    }while (i<100);
    return a;
}
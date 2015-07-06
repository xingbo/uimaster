{
    int k = 0;
    for(int i = 0,j = 10000; i < 10000 && j > 0; i++,j--)
    {
        k = k + i + j;
    }
    return k;
}
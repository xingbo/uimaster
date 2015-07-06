{
    int a = 1;
    {
        int b = a + 1;
        {
            int g = b + a;
            return g;
        }
    }
}
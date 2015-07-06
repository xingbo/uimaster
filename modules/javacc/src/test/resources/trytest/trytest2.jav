String {
    try
    {
        throw new RuntimeException("throw exception!");
    }
    catch(Exception e)
    {
    	System.out.println("catch block exception: "+e.getMessage());
        return e.getMessage();
    }
}
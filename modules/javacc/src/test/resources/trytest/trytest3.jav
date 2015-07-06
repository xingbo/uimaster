{
    try
    {
        throw new Exception("throw block exception");
    }
    catch(Exception e)
    {
    	throw new Exception("catch block exception");
    }
    finally
    {
        return "finally return";
    }
}
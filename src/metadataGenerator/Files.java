package metadataGenerator;

public class Files
{
    private String doc_class;

    private String doc_id;

    private String path;

    public String getDoc_class ()
    {
        return doc_class;
    }

    public void setDoc_class (String doc_class)
    {
        this.doc_class = doc_class;
    }

    public String getDoc_id ()
    {
        return doc_id;
    }

    public void setDoc_id (String doc_id)
    {
        this.doc_id = doc_id;
    }

    public String getPath ()
    {
        return path;
    }

    public void setPath (String path)
    {
        this.path = path;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [doc_class = "+doc_class+", doc_id = "+doc_id+", path = "+path+"]";
    }
}
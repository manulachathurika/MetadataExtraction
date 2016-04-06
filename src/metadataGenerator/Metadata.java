package metadataGenerator;

public class Metadata
{
    private Files[] files;

    private String sms_type;

    private String cpp_name;

    private String[] ul_remarks;

    private boolean print_tab;

    private String pol_num;

    private String division;

    private String source;

    private String ct_name;

    private String ref_num;

    private String sub_type;

    private String fast_t;

    private String pro_code;

    private String ul_stat;

    private String source_date;

    public Files[] getFiles ()
    {
        return files;
    }

    public void setFiles (Files[] files)
    {
        this.files = files;
    }

    public String getSms_type ()
    {
        return sms_type;
    }

    public void setSms_type (String sms_type)
    {
        this.sms_type = sms_type;
    }

    public String getCpp_name ()
    {
        return cpp_name;
    }

    public void setCpp_name (String cpp_name)
    {
        this.cpp_name = cpp_name;
    }

    public String[] getUl_remarks ()
    {
        return ul_remarks;
    }

    public void setUl_remarks (String[] ul_remarks)
    {
        this.ul_remarks = ul_remarks;
    }

    public boolean getPrint_tab ()
    {
        return print_tab;
    }

    public void setPrint_tab (boolean print_tab)
    {
        this.print_tab = print_tab;
    }

    public String getPol_num ()
    {
        return pol_num;
    }

    public void setPol_num (String pol_num)
    {
        this.pol_num = pol_num;
    }

    public String getDivision ()
    {
        return division;
    }

    public void setDivision (String division)
    {
        this.division = division;
    }

    public String getSource ()
    {
        return source;
    }

    public void setSource (String source)
    {
        this.source = source;
    }

    public String getCt_name ()
    {
        return ct_name;
    }

    public void setCt_name (String ct_name)
    {
        this.ct_name = ct_name;
    }

    public String getRef_num ()
    {
        return ref_num;
    }

    public void setRef_num (String ref_num)
    {
        this.ref_num = ref_num;
    }

    public String getSub_type ()
    {
        return sub_type;
    }

    public void setSub_type (String sub_type)
    {
        this.sub_type = sub_type;
    }

    public String getFast_t ()
    {
        return fast_t;
    }

    public void setFast_t (String fast_t)
    {
        this.fast_t = fast_t;
    }

    public String getPro_code ()
    {
        return pro_code;
    }

    public void setPro_code (String pro_code)
    {
        this.pro_code = pro_code;
    }

    public String getUl_stat ()
    {
        return ul_stat;
    }

    public void setUl_stat (String ul_stat)
    {
        this.ul_stat = ul_stat;
    }

    public String getSource_date ()
    {
        return source_date;
    }

    public void setSource_date (String source_date)
    {
        this.source_date = source_date;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [files = "+files+", sms_type = "+sms_type+", cpp_name = "+cpp_name+", ul_remarks = "+ul_remarks+", print_tab = "+print_tab+", pol_num = "+pol_num+", division = "+division+", source = "+source+", ct_name = "+ct_name+", ref_num = "+ref_num+", sub_type = "+sub_type+", fast_t = "+fast_t+", pro_code = "+pro_code+", ul_stat = "+ul_stat+", source_date = "+source_date+"]";
    }
}

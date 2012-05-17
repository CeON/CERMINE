package pl.edu.icm.yadda.analysis.parsing.generic_citation_to_mallet_adapter;

public enum Feature {
    /* public static final String */ TAG_STARTOWY{
    public String toString() {
        return "F1";
    }
}, /* =  "F01"; */ //"TAG_STARTOWY";
    /* public static final String */ TAG_KONCOWY{
    public String toString() {
        return "F2";
    }
}, /* =  "F02"; */ //"TAG_KONCOWY";    
/**//* public static final String */ LANCUCH_CYFR{
    public String toString() {
        return "F3";
    }
}, /* =  "F03"; */ //"LANCUCH_CYFR";
    /* public static final String */ LANCUCH_LITER{
    public String toString() {
        return "F4";
    }
}, /* =  "F04"; */ //"LANCUCH_LITER";    
/**//* public static final String */ MA_DLUGOSC_DWA{
    public String toString() {
        return "F5";
    }
}, /* =  "F05"; */ //"MA_DLUGOSC_DWA";
    /* public static final String */ MA_DLUGOSC_JEDEN{
    public String toString() {
        return "F6";
    }
}, /* =  "F06"; */ //"MA_DLUGOSC_JEDEN";
/**//* public static final String */ JEST_APOSTROFEM{
    public String toString() {
        return "F7";
    }
}, /* =  "F07"; */ //"JEST_APOSTROFEM";
    /* public static final String */ JEST_NAWIASEM_KWADRATOWYM_POCZ{
    public String toString() {
        return "F8";
    }
}, /* =  "F08"; */ //"JEST_NAWIASEM_KWADRATOWYM_POCZ";
    /* public static final String */ JEST_NAWIASEM_KWADRATOWYM_KONC{
    public String toString() {
        return "F9";
    }
}, /* =  "F09"; */ //"JEST_NAWIASEM_KWADRATOWYM_KONC";
    /* public static final String */ JEST_NAWIASEM_OKRAGLYM_POCZ{
    public String toString() {
        return "F10";
    }
}, /* =  "F10"; */ //"JEST_NAWIASEM_OKRAGLYM_POCZ";
    /* public static final String */ JEST_NAWIASEM_OKRAGLYM_KONC{
    public String toString() {
        return "F11";
    }
}, /* =  "F11"; */ //"JEST_NAWIASEM_OKRAGLYM_KONC";
    /* public static final String */ JEST_NAWIASEM_KLAMROWYM_POCZ{
    public String toString() {
        return "F12";
    }
}, /* =  "F12"; */ //"JEST_NAWIASEM_KLAMROWYM_POCZ";
    /* public static final String */ JEST_NAWIASEM_KLAMROWYM_KONC{
    public String toString() {
        return "F13";
    }
}, /* =  "F13"; */ //"JEST_NAWIASEM_KLAMROWYM_KONC";
    /* public static final String */ JEST_NAWIASEM_KATOWYM_POCZ{
    public String toString() {
        return "F14";
    }
}, /* =  "F14"; */ //"JEST_NAWIASEM_KATOWYM_POCZ";
    /* public static final String */ JEST_NAWIASEM_KATOWYM_KONC{
    public String toString() {
        return "F15";
    }
}, /* =  "F15"; */ //"JEST_NAWIASEM_KATOWYM_KONC";
    /* public static final String */ JEST_DWUKROPKIEM{
    public String toString() {
        return "F16";
    }
}, /* =  "F16"; */ //"JEST_DWUKROPKIEM";
    /* public static final String */ JEST_PRZECINKIEM{
    public String toString() {
        return "F17";
    }
}, /* =  "F17"; */ //"JEST_PRZECINKIEM";
    /* public static final String */ JEST_MYSLNIKIEM{
    public String toString() {
        return "F18";
    }
}, /* =  "F18"; */ //"JEST_MYSLNIKIEM";
    /* public static final String */ JEST_WYKRZYKNIKIEM{
    public String toString() {
        return "F19";
    }
}, /* =  "F19"; */ //"JEST_WYKRZYKNIKIEM";
    /* public static final String */ JEST_KROPKA{
    public String toString() {
        return "F20";
    }
}, /* =  "F20"; */ //"JEST_KROPKA";
    /* public static final String */ JEST_ZNAKIEM_ZAPYTANIA{
    public String toString() {
        return "F21";
    }
}, /* =  "F21"; */ //"JEST_ZNAKIEM_ZAPYTANIA";
    /* public static final String */ JEST_PYTAJNIKIEM{
    public String toString() {
        return "F22";
    }
}, /* =  "F22"; */ //JEST_ZNAKIEM_ZAPYTANIA;
    /* public static final String */ JEST_SREDNIKIEM{
    public String toString() {
        return "F23";
    }
}, /* =  "F23"; */ //"JEST_SREDNIKIEM";
    /* public static final String */ JEST_SLASHEM{
    public String toString() {
        return "F24";
    }
}, /* =  "F24"; */ //"JEST_SLASHEM";
    /* public static final String */ JEST_BACK_SLASHEM{
    public String toString() {
        return "F25";
    }
}, /* =  "F25"; */ //"JEST_BACK_SLASHEM";
    /* public static final String */ JEST_AMPERSANDEM{
    public String toString() {
        return "F26";
    }
}, /* =  "F26"; */ //"JEST_AMPERSANDEM";
    /* public static final String */ JEST_MALPA{
    public String toString() {
        return "F27";
    }
}, /* =  "F27"; */ //"JEST_MALPA";
    /* public static final String */ JEST_GWIAZDKA{
    public String toString() {
        return "F28";
    }
}, /* =  "F28"; */ //"JEST_GWIAZDKA";
    /* public static final String */ JEST_ASTERIKSEM{
    public String toString() {
        return "F29";
    }
}, /* =  "F29"; */ //JEST_GWIAZDKA;
    /* public static final String */ JEST_DASZKIEM{
    public String toString() {
        return "F30";
    }
}, /* =  "F30"; */ //"JEST_DASZKIEM";
    /* public static final String */ JEST_CARET{
    public String toString() {
        return "F31";
    }
}, /* =  "F31"; */ //JEST_DASZKIEM;
    //NIE OBSLUGUJEMY ZNAKU COPYRIGHTU - TO DOBRZE CZY ZLE?
    //NIE OBSLUGUJEMY ZNAKU (DOUBLE)DAGGER - TO DOBRZE CZY ZLE?
/**//* public static final String */ JEST_PROCENTEM{
    public String toString() {
        return "F32";
    }
}, /* =  "F32"; */ //"JEST_PROCENTEM";
    /* public static final String */ JEST_TYLDA{
    public String toString() {
        return "F33";
    }
}, /* =  "F33"; */ //"JEST_TYLDA";    
/**//* public static final String */ JEST_HASHEM{
    public String toString() {
        return "F34";
    }
}, /* =  "F34"; */ //"JEST_HASHEM";
    /* public static final String */ JEST_DOLAREM{
    public String toString() {
        return "F35";
    }
}, /* =  "F35"; */ //"JEST_DOLAREM";
    /* public static final String */ JEST_ROWNA_SIE{
    public String toString() {
        return "F36";
    }
}, /* =  "F36"; */ //"JEST_ROWNA_SIE";
    /* public static final String */ JEST_CUDZYSLOWEM{
    public String toString() {
        return "F37";
    }
}, /* =  "F37"; */ //"JEST_CUDZYSLOWEM";
    /* public static final String */ JEST_KRESKA{
    public String toString() {
        return "F38";
    }
}, /* =  "F38"; */ //"JEST_KRESKA";    
/**//* public static final String */ ZAWIERA_ROK{
    public String toString() {
        return "F39";
    }
}, /* =  "F39"; */ //"ZAWIERA_ROKIEM";
    /* public static final String */ ZAWIERA_MALE_ZNAKI{
    public String toString() {
        return "F40";
    }
}, /* =  "F40"; */ //"ZAWIERA_MALE_ZNAKI";
    /* public static final String */ ZAWIERA_DUZE_ZNAKI{
    public String toString() {
        return "F41";
    }
}, /* =  "F41"; */ //"ZAWIERA_DUZE_ZNAKI";    
/**//* public static final String */ ZACZYNA_SIE_Z_DUZEJ_LITERY{
    public String toString() {
        return "F42";
    }
}, /* =  "F42"; */ //"ZACZYNA_SIE_Z_DUZEJ_LITERY";
    /* public static final String */ ZACZYNA_SIE_Z_MALEJ_LITERY{
    public String toString() {
        return "F43";
    }
}, /* =  "F43"; */ //"ZACZYNA_SIE_Z_MALEJ_LITERY";
    //ilość cech.. iC.. iC = 48-3-2=43 cechy. 
    //jeżeli uwzględnić sąsiadowanie cech to przy ilosci sasiadow.. iS.. robi się ogolem cech.. oC.. oC=(iS+1)*iC
    //przyjmijmy 3 sasiadow. oC=129+129+43=301.
    //przyklad zadany ze strony GRMMa ma rzutowanie 27cech 
}

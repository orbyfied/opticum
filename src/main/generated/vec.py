import _gencodelib

vec_comp_chars = [ 'x', 'y', 'z', 'w' ]

def write_vec_source(dim, t, tchar):
    # create class name
    classname  = "Vec" + str(dim) + tchar
    package    = "net.orbyfied.opticum.util"
    fullcl, fp = _gencodelib.class_src_location(package, classname)

    comps = vec_comp_chars[0:dim]

    # create component values
    comp_decl_all = ""
    for comp in comps:
        comp_decl_all += comp + ", "
    comp_decl_all = comp_decl_all[0:(len(comp_decl_all) - 2)]

    comp_args_all = ""
    for comp in comps:
        comp_args_all += t + " " + comp + ", "
    comp_args_all = comp_args_all[0:(len(comp_args_all) - 2)]

    comp_all_set = ""
    for comp in comps:
        comp_all_set += "this." + comp + " = " + comp + ";\n\t\t"

    comp_getters = ""
    for comp in comps:
        comp_getters += "\tpublic " + t + " get" + comp.capitalize() + "() { return this." + comp + "; }\n"

    comp_setters = ""
    for comp in comps:
         comp_setters += "\tpublic " + classname + " set" + comp.capitalize() + "(" + t + " " + comp + ") { this." + comp + " = " + comp + "; return this; }\n"

    # create rest
    rest = ""

    tostrexpr = "\"(\""
    for comp in comps:
        tostrexpr += " + " + comp + " + \", \""
    tostrexpr = tostrexpr[0:-6]
    tostrexpr += " + \")\""
    rest += "\t@Override\n\tpublic String toString() { return " + tostrexpr + "; }\n"

    eqexpr = ""
    for comp in comps:
        eqexpr += "other." + comp + " == " + comp + " && "
    eqexpr += "true"
    rest += "\t@Override\n\tpublic boolean equals(Object o) {\n\t\t"
    rest += "if (o == null) return false;\n\t\t"
    rest += "if (o == this) return true;\n\t\t"
    rest += "if (o.getClass() != getClass()) return false;\n\t\t"
    rest += (classname + " other = (" + classname + ") o;\n\t\t")
    rest += "return " + eqexpr + ";\n\t"
    rest += "}"

    hcexpr = "Objects.hash("
    for comp in comps:
        hcexpr += comp + ","
    hcexpr = hcexpr[0:-1]
    hcexpr += ")"
    rest += "\t@Override\n\tpublic int hashCode() { return " + hcexpr + "; }" 

    imports = ""
    imports += "import java.util.Objects;"

    # create dict values
    v = {
        "N"  : classname,
        "TY" : t,
        "T"  : tchar,
        "D"  : str(dim),

        "COMP_DECL_ALL" : comp_decl_all,
        "COMP_ALL_ARGS" : comp_args_all,
        "COMP_ALL_SET"  : comp_all_set,
        "COMP_GETTERS"  : comp_getters,
        "COMP_SETTERS" : comp_setters,

        "IMPORTS" : imports,
        "REST" : rest,
    }

    # parse template
    txt = _gencodelib.read_and_fill_template(package, "vec_template.txt", v)

    # write text
    _gencodelib.write_result_source(fp, txt)

d_types = [ 'float=f', 'double=d', 'int=i', 'long=l' ]
d_dims  = [ 1, 2, 3, 4 ]

for ts in d_types:
    t  = ts.split('=')[0]
    tc = ts.split('=')[1]
    for d in d_dims:
        write_vec_source(d, t, tc)

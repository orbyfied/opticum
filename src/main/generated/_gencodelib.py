import os

# returns: (full name, file path)
def class_src_location(package : str, name : str):
    # create full name
    fn = package + "." + name
    # create file path
    fp = package.replace(".", "/") + "/" + name + ".java"

    return fn, fp

def write_result_source(name : str, txt : str):
    # get result file
    res_dir  = os.getenv("RESULT_DIR")
    res_file = os.path.join(res_dir, name)
    # create result file
    if not os.path.exists(os.path.dirname(res_file)):
        os.makedirs(os.path.dirname(res_file))
    # write to result file
    f = open(res_file, 'w')
    f.write(txt)
    f.flush()
    f.close()

def read_and_fill_template(package : str, template_file : str, fill : dict):
    # read template
    f = open(template_file, 'r')
    t = f.read()
    f.close()
    # append package statement
    t = "package " + package + ";\n\n" + t
    # replace all values
    for k in fill:
        v = fill[k]
        t = t.replace("{" + k + "}", v)
    # return text
    return t
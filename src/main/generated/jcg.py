##########################
# Utilities
##########################
from abc import ABC


def str_join_list(arr, sep : str):
    s = ""
    i = 0
    l = len(arr)
    for e in arr:
        s += str(e)
        if i != l:
            s += sep
        i += 1
    return s

##########################
# Elements
##########################

ModifierOrder = [
    'public', 'private', 'protected',
    'static',
    'final', 'synchronized'
]

class ModifierHolder:
    def __init__(self):
        self.modifiers = []

    def with_modifier(self, mod):
        self.modifiers.append(mod)
        return self

    def without_modifier(self, mod):
        self.modifiers.append(mod)
        return self

    def sorted_modifiers(self):
        return sorted(self.modifiers, key= lambda x: ModifierOrder.index(x))

    def create_string(self):
        mods = self.sorted_modifiers()
        s    = ""
        for mod in mods:
            s += mod + " "
        return str[:-1]

class CodeNode:
    def __init__(self):
        self.children = []

    def to_string(self):
        raise NotImplementedError()

##########################
# Builder
##########################

class ClassBuilder(ModifierHolder, CodeNode):
    def __init__(self, name):
        super().__init__()

        # declaration
        self.name = ""
        self.extends    = None
        self.implements = []

        # members
        self.methods = []
        self.fields  = []
        self.constructors = []

    def get_name(self):
        return self.name

    def with_extends(self, e : str):
        self.extends = e
        return self

    def with_implements(self, i : str):
        self.implements.append(i)
        return self

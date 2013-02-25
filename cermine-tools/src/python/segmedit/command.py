#!/usr/bin/env python
# encoding: utf-8

###
# Copyright 2011 University of Warsaw, Krzysztof Rusek
# 
# This file is part of SegmEdit.
#
# SegmEdit is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# SegmEdit is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with SegmEdit.  If not, see <http://www.gnu.org/licenses/>.


class Command(object):
    """
    Base class for undoable commands.
    
    Each command should, apart from undo and redo methods, override __nonzero__
    method so that it returns true if executing command has side effects and
    false otherwise.
    """

    def redo(self):
        raise NotImplementedError('Abstract method')

    def undo(self):
        raise NotImplementedError('Abstract method')


class CompositeCommand(Command):
    """
    List of commands.
    """

    def __init__(self):
        self._cmds = []

    def __len__(self):
        return len(self._cmds)

    def append(self, command):
        """
        Appends command to end if it has any side effects.
        """
        if command:
            self._cmds.append(command)

    def redo(self):
        for cmd in self._cmds:
            cmd.redo()

    def undo(self):
        for cmd in reversed(self._cmds):
            cmd.undo()

    def clear(self):
        del self._cmds[:]


class SetAttributeCommand(Command):
    """
    Attribute setting command.
    
    If old value equals new value, the command is said to have no side effects.
    """

    def __init__(self, obj, name, value):
        self._obj = obj
        self._name = name
        self._oldValue = getattr(obj, name)
        self._newValue = value

    def __nonzero__(self):
        return self._oldValue != self._newValue

    def redo(self):
        setattr(self._obj, self._name, self._newValue)

    def undo(self):
        setattr(self._obj, self._name, self._oldValue)


class SetItemCommand(Command):
    """
    Item setting command.
    
    If old value equals new value, the command is said to have no side effects.
    """

    def __init__(self, obj, key, value):
        self._obj = obj
        self._key = key
        self._oldValue = obj[key]
        self._newValue = value

    def __nonzero__(self):
        return self._oldValue != self._newValue

    def redo(self):
        self._obj[self._key] = self._newValue

    def undo(self):
        self._obj[self._key] = self._oldValue


class CommandManager(object):
    """
    Command manager containing undo and redo list.
    
    Commands can be added by calling execute method which automatically
    executes given command. Recently added commands can be undone by calling
    rollback method and moved to undo stack by calling commit method.
    """
    def __init__(self, limit=None):
        self._undoList = []
        self._redoList = []
        self._limit = limit
        self._cleanDelta = 0
        self._transaction = CompositeCommand()

    def clean(self):
        """
        Marks current state as clean.
        """
        self._cleanDelta = 0

    def isClean(self):
        return self._cleanDelta == 0

    def execute(self, command):
        command.redo()
        self._transaction.append(command)

    def setAttribute(self, obj, name, value):
        self.execute(SetAttributeCommand(obj, name, value))

    def setItem(self, obj, key, value):
        self.execute(SetItemCommand(obj, key, value))

    def commit(self):
        if self._transaction:
            self._undoList.append(self._transaction)
            self._transaction = CompositeCommand()
            del self._redoList[:]
            if self._limit is not None and len(self._undoList) > self._limit:
                del self._undoList[0]
            if self._cleanDelta is not None:
                if self._cleanDelta >= 0:
                    self._cleanDelta += 1
                else:
                    self._cleanDelta = None
            return True
        else:
            return False

    def rollback(self):
        if self._transaction:
            self._transaction.undo()
            self._transaction.clear()
            return True
        else:
            return False

    def canUndo(self):
        return bool(self._undoList)

    def undo(self):
        if self._undoList:
            self.rollback()
            cmd = self._undoList.pop()
            cmd.undo()
            self._redoList.append(cmd)
            if self._cleanDelta is not None:
                self._cleanDelta -= 1
            return True
        else:
            return False

    def canRedo(self):
        return bool(self._redoList)

    def redo(self):
        if self._redoList:
            self.rollback()
            cmd = self._redoList.pop()
            cmd.redo()
            self._undoList.append(cmd)
            if self._cleanDelta is not None:
                self._cleanDelta += 1
            return True
        else:
            return False


class EmptyCommandManager:

    def execute(self, command):
        command.redo()

    def setAttribute(self, obj, name, value):
        setattr(obj, name, value)

    def setItem(self, obj, key, value):
        obj[key] = value

    def commit(self):
        return False

    def rollback(self):
        return False

    def canUndo(self):
        return False

    def undo(self):
        return False

    def canRedo(self):
        return False

    def redo(self):
        return False


if __name__ == "__main__":
    print "This is a module, don't run it as a program"

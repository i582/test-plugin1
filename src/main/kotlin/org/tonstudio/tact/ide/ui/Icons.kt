package org.tonstudio.tact.ide.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader

object Icons {
    val Tact = IconLoader.getIcon("/icons/tact.svg", this::class.java)
    val Module = AllIcons.FileTypes.Config

    val Test = AllIcons.RunConfigurations.TestState.Run
    val TestGreen = AllIcons.RunConfigurations.TestState.Green2
    val TestRed = AllIcons.RunConfigurations.TestState.Red2

    val Struct = AllIcons.Nodes.Class
    val Interface = AllIcons.Nodes.Interface
    val Enum = AllIcons.Nodes.Enum
    val Union = AllIcons.Nodes.AbstractClass // TODO: change
    val Trait = AllIcons.Nodes.Test // TODO: change
    val Contract = AllIcons.Nodes.Class // TODO: change
    val Primitive = AllIcons.Nodes.Parameter // TODO: change
    val Message = AllIcons.Nodes.Method // TODO: change
    val Method = AllIcons.Nodes.Method
    val StaticMethod = AllIcons.Nodes.Static
    val Function = AllIcons.Nodes.Function
    val Variable = AllIcons.Nodes.Variable
    val ModuleVariable = AllIcons.Nodes.Gvariable
    val Constant = AllIcons.Nodes.Constant
    val Parameter = AllIcons.Nodes.Parameter
    val Field = AllIcons.Nodes.Field
    val Receiver = AllIcons.Nodes.Parameter
    val Alias = AllIcons.Nodes.Alias
    val Directory = AllIcons.Nodes.Folder

    val SourceRoot = AllIcons.Modules.SourceRoot
    val LocalModulesRoot = AllIcons.Nodes.ModuleGroup
}

package com.androhi.androiddrawableviewer.form;

import com.androhi.androiddrawableviewer.Constants;
import com.androhi.androiddrawableviewer.PluginConfig;
import com.androhi.androiddrawableviewer.util.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SettingsDialog extends DialogWrapper {

    private Project project;
    private JPanel mainPanel;
    private TextFieldWithBrowseButton resDirText;
    private JCheckBox checkMdpi;
    private JCheckBox checkHdpi;
    private JCheckBox checkXhdpi;
    private JCheckBox checkXxhdpi;
    private JCheckBox checkXxxhdpi;
    private PluginConfig pluginConfig;

    public SettingsDialog(Project project) {
        super(project, true);

        this.project = project;
        setTitle("Settings");
        setResizable(true);

        buildViews();
        init();
    }

    private void buildViews() {
        pluginConfig = PluginConfig.getInstance(project);
        if (project == null || pluginConfig == null) return;

        String savedResDir = pluginConfig.getResDir();
        if (savedResDir == null) {
            savedResDir = project.getBasePath() + Constants.DEFAULT_RESOURCE_PATH;
        }

        resDirText.setText(savedResDir);
        VirtualFile selectDir = VirtualFileManager.getInstance().findFileByUrl(savedResDir);
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        descriptor.setRoots(selectDir);
        resDirText.addBrowseFolderListener(new TextBrowseFolderListener(descriptor, project));

        checkMdpi.setSelected(pluginConfig.isMdpi());
        checkHdpi.setSelected(pluginConfig.isHdpi());
        checkXhdpi.setSelected(pluginConfig.isXhdpi());
        checkXxhdpi.setSelected(pluginConfig.isXxhdpi());
        checkXxxhdpi.setSelected(pluginConfig.isXxxhdpi());
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String resDirString = resDirText.getText();
        if (resDirString == null || resDirString.isEmpty()) {
            return new ValidationInfo("Select resource directory.");
        }

        if (!checkMdpi.isSelected() && !checkHdpi.isSelected() &&
                !checkXhdpi.isSelected() && !checkXxhdpi.isSelected() && !checkXxxhdpi.isSelected()) {
            return new ValidationInfo("Check any box.");
        }
        return null;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        String resDirString = resDirText.getText();
        pluginConfig.setResDir(resDirString);
        pluginConfig.setMdpi(checkMdpi.isSelected());
        pluginConfig.setHdpi(checkHdpi.isSelected());
        pluginConfig.setXhdpi(checkXhdpi.isSelected());
        pluginConfig.setXxhdpi(checkXxhdpi.isSelected());
        pluginConfig.setXxxhdpi(checkXxxhdpi.isSelected());
        resetContent(project);
    }

    private void resetContent(Project project) {
        DrawableViewer drawableViewer = new DrawableViewer(project);
        ContentManager contentManager = ToolWindowManager.getInstance(project)
                .getToolWindow(Constants.TOOL_WINDOW_ID).getContentManager();
        Content content = contentManager.getFactory().createContent(drawableViewer, null, false);

        contentManager.removeAllContents(true);
        contentManager.addContent(content);
    }
}

/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.tools;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.archimatetool.editor.ui.components.ExtendedTitleAreaDialog;
import com.archimatetool.editor.ui.textrender.TextRenderer;
import com.archimatetool.model.IFeaturesEList;
import com.archimatetool.model.IProfile;

/**
 * Dialog to customise label expression for selected Profiles
 */
public class LabelExpressionDialog extends ExtendedTitleAreaDialog {

    private final List<IProfile> profiles;
    private Text textExpr;

    public LabelExpressionDialog(Shell parentShell, List<IProfile> profiles) {
        super(parentShell, "LabelExpressionDialog"); //$NON-NLS-1$
        this.profiles = profiles;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.LabelExpressionDialog_0);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.LabelExpressionDialog_0);
        setMessage(Messages.LabelExpressionDialog_1);

        Composite composite = (Composite)super.createDialogArea(parent);

        Composite client = new Composite(composite, SWT.NULL);
        client.setLayout(new GridLayout(1, false));
        GridDataFactory.create(GridData.FILL_BOTH).applyTo(client);

        Label label = new Label(client, SWT.NONE);
        label.setText(Messages.LabelExpressionDialog_2);

        textExpr = new Text(client, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 140;
        textExpr.setLayoutData(gd);

        // Pre-fill if all selected profiles share same expression
        String initial = getCommonExpression();
        if(initial != null) {
            textExpr.setText(initial);
        }

        return composite;
    }

    private String getCommonExpression() {
        String common = null;
        for(IProfile p : profiles) {
            String value = p.getFeatures().getString(TextRenderer.FEATURE_NAME, null);
            if(common == null) {
                common = value;
            }
            else {
                if((common == null && value != null) || (common != null && !common.equals(value))) {
                    return null; // different values -> leave blank
                }
            }
        }
        return common;
    }

    @Override
    protected void okPressed() {
        String expr = textExpr.getText().trim();
        for(IProfile p : profiles) {
            IFeaturesEList features = p.getFeatures();
            if(expr.isEmpty()) {
                features.remove(TextRenderer.FEATURE_NAME);
            }
            else {
                features.putString(TextRenderer.FEATURE_NAME, expr);
            }
        }
        super.okPressed();
    }
}

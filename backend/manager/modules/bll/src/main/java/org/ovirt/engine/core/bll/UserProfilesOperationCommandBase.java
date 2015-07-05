package org.ovirt.engine.core.bll;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.ovirt.engine.core.bll.context.CommandContext;
import org.ovirt.engine.core.bll.utils.PermissionSubject;
import org.ovirt.engine.core.common.VdcObjectType;
import org.ovirt.engine.core.common.action.UserProfileParameters;
import org.ovirt.engine.core.common.errors.EngineMessage;
import org.ovirt.engine.core.dao.UserProfileDao;
import org.ovirt.engine.core.uutils.ssh.OpenSSHUtils;


public abstract class UserProfilesOperationCommandBase<T extends UserProfileParameters> extends CommandBase<T> {

    static final private String SSH_RSA = "ssh-rsa";

    @Inject
    protected UserProfileDao userProfileDao;

    public UserProfilesOperationCommandBase(T parameters) {
        this(parameters, null);
    }

    public UserProfilesOperationCommandBase(T parameters, CommandContext commandContext) {
        super(parameters, commandContext);
    }

    @Override
    protected boolean canDoAction() {
        String sshPublicKey = getParameters().getUserProfile().getSshPublicKey();

        if (sshPublicKey == null || sshPublicKey.isEmpty()) {
            // the user wants to wipe out its own key, and we're fine with that.
            return true;
        }

        // else it is either a new or replacement key. In both cases, must be a valid key.
        if (!OpenSSHUtils.isPublicKeyValid(sshPublicKey)) {
            return failCanDoAction(EngineMessage.ACTION_TYPE_FAILED_INVALID_PUBLIC_SSH_KEY);
        }

        return true;
    }

    @Override
    public List<PermissionSubject> getPermissionCheckSubjects() {
        return Collections.singletonList(new PermissionSubject(getUserId(),
                                                               VdcObjectType.System,
                                                               getActionType().getActionGroup()));
    }
}

package org.ovirt.engine.core.bll.validator.storage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.ovirt.engine.core.bll.Backend;
import org.ovirt.engine.core.bll.ValidationResult;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.businessentities.VMStatus;
import org.ovirt.engine.core.common.businessentities.VmDevice;
import org.ovirt.engine.core.common.businessentities.storage.Disk;
import org.ovirt.engine.core.common.businessentities.storage.DiskStorageType;
import org.ovirt.engine.core.common.businessentities.storage.DiskVmElement;
import org.ovirt.engine.core.common.businessentities.storage.LunDisk;
import org.ovirt.engine.core.common.businessentities.storage.ScsiGenericIO;
import org.ovirt.engine.core.common.constants.StorageConstants;
import org.ovirt.engine.core.common.errors.EngineMessage;
import org.ovirt.engine.core.common.interfaces.VDSBrokerFrontend;
import org.ovirt.engine.core.common.utils.Pair;
import org.ovirt.engine.core.dal.dbbroker.DbFacade;
import org.ovirt.engine.core.dao.VmDao;
import org.ovirt.engine.core.utils.ReplacementUtils;

/**
 * A validator for the {@link Disk} class.
 *
 */
public class DiskValidator {

    private final Disk disk;

    protected static final String DISK_NAME_VARIABLE = "DiskName";
    protected static final String VM_LIST = "VmList";
    protected static final String VM_NAME_VARIABLE = "VmName";

    public DiskValidator(Disk disk) {
        this.disk = disk;
    }

    public ValidationResult isDiskPluggedToVmsThatAreNotDown(boolean checkOnlyVmsSnapshotPluggedTo, List<Pair<VM, VmDevice>> vmsForDisk) {
        if (vmsForDisk == null) {
            vmsForDisk = getVmDao().getVmsWithPlugInfo(disk.getId());
        }

        for (Pair<VM, VmDevice> pair : vmsForDisk) {
            VmDevice vmDevice = pair.getSecond();

            if (checkOnlyVmsSnapshotPluggedTo && vmDevice.getSnapshotId() == null) {
                continue;
            }

            VM currVm = pair.getFirst();
            if (VMStatus.Down != currVm.getStatus()) {
                if (vmDevice.getIsPlugged()) {
                    return new ValidationResult(EngineMessage.ACTION_TYPE_FAILED_VM_IS_NOT_DOWN);
                }
            }
        }

        return ValidationResult.VALID;

    }

    public ValidationResult isDiskUsedAsOvfStore() {
        if (disk.isOvfStore()) {
            return new ValidationResult(EngineMessage.ACTION_TYPE_FAILED_OVF_DISK_NOT_SUPPORTED);
        }
        return ValidationResult.VALID;
    }

    protected VmDao getVmDao() {
        return DbFacade.getInstance().getVmDao();
    }

    public ValidationResult validateUnsupportedDiskStorageType(DiskStorageType... diskStorageTypes) {
        List<DiskStorageType> diskStorageTypeList = Arrays.asList(diskStorageTypes);
        if (diskStorageTypeList.contains(disk.getDiskStorageType())) {
            return new ValidationResult(EngineMessage.ACTION_TYPE_FAILED_NOT_SUPPORTED_DISK_STORAGE_TYPE,
                    String.format("$diskStorageType %s", disk.getDiskStorageType()));
        }
        return ValidationResult.VALID;
    }

    protected VDSBrokerFrontend getVdsBroker() {
        return Backend.getInstance().getResourceManager();
    }

    public ValidationResult validateNotHostedEngineDisk() {
        return isHostedEngineDirectLunDisk() ? new ValidationResult(EngineMessage.ACTION_TYPE_FAILED_HOSTED_ENGINE_DISK) :
                ValidationResult.VALID;
    }

    private boolean isHostedEngineDirectLunDisk() {
        return disk.getDiskStorageType() == DiskStorageType.LUN &&
                    StorageConstants.HOSTED_ENGINE_LUN_DISK_ALIAS.equals(disk.getDiskAlias());
    }

    public ValidationResult isUsingScsiReservationValid(VM vm, DiskVmElement dve, LunDisk lunDisk) {
        // this operation is valid only when attaching disk to VMs
        if (vm == null && dve.isUsingScsiReservation()) {
            return new ValidationResult(EngineMessage.ACTION_TYPE_FAILED_SCSI_RESERVATION_NOT_VALID_FOR_FLOATING_DISK);
        }
        // scsi reservation can be enabled only when sgio is unfiltered
        if (dve.isUsingScsiReservation() && lunDisk.getSgio() == ScsiGenericIO.FILTERED) {
            return new ValidationResult(EngineMessage.ACTION_TYPE_FAILED_SGIO_IS_FILTERED);
        }

        return  ValidationResult.VALID;
    }

    public ValidationResult validRemovableHostedEngineDisks(VM vm) {
        return isHostedEngineDirectLunDisk() || !vm.isHostedEngine()
                ? ValidationResult.VALID
                : new ValidationResult(EngineMessage.ACTION_TYPE_FAILED_HOSTED_ENGINE_DISK);
    }

    public ValidationResult isDiskExists() {
        if (disk == null) {
            return new ValidationResult(EngineMessage.ACTION_TYPE_FAILED_DISK_NOT_EXIST);
        }
        return ValidationResult.VALID;
    }

    public ValidationResult isDiskAttachedToVm(VM vm) {
        List<VM> vms = getVmDao().getVmsListForDisk(disk.getId(), true);
        String[] replacements = {ReplacementUtils.createSetVariableString(DISK_NAME_VARIABLE, disk.getDiskAlias()),
                ReplacementUtils.createSetVariableString(VM_NAME_VARIABLE, vm.getName())};
        return ValidationResult.failWith(EngineMessage.ACTION_TYPE_FAILED_DISK_NOT_ATTACHED_TO_VM, replacements).
                when(vms.stream().noneMatch(vm1 -> vm1.getId().equals(vm.getId())));
    }

    public ValidationResult isDiskAttachedToAnyVm() {
        List<VM> vms = getVmDao().getVmsListForDisk(disk.getId(), true);
        if (!vms.isEmpty()) {
            return new ValidationResult(EngineMessage.ACTION_TYPE_FAILED_DISK_ATTACHED_TO_VMS,
                    ReplacementUtils.createSetVariableString(DISK_NAME_VARIABLE, disk.getDiskAlias()),
                    ReplacementUtils.createSetVariableString(VM_LIST,
                            vms.stream().map(VM::getName).collect(Collectors.joining(","))));

        }
        return ValidationResult.VALID;
    }

    public ValidationResult isVmNotContainsBootDisk(VM vm) {
        Disk bootDisk = DbFacade.getInstance().getDiskDao().getVmBootActiveDisk(vm.getId());
        if (bootDisk != null) {
            return new ValidationResult(EngineMessage.ACTION_TYPE_FAILED_DISK_BOOT_IN_USE,
                    ReplacementUtils.createSetVariableString("VmName", vm.getName()),
                    ReplacementUtils.createSetVariableString("DiskName", bootDisk.getDiskAlias()));
        }
        return ValidationResult.VALID;
    }
}

package org.ovirt.engine.ui.uicommonweb.models.vms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ovirt.engine.core.common.businessentities.DisplayType;
import org.ovirt.engine.core.common.businessentities.StoragePool;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VDSGroup;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.businessentities.VMStatus;
import org.ovirt.engine.core.common.businessentities.VmWatchdog;
import org.ovirt.engine.core.common.businessentities.network.VmNetworkInterface;
import org.ovirt.engine.core.common.queries.ConfigurationValues;
import org.ovirt.engine.core.common.queries.IdQueryParameters;
import org.ovirt.engine.core.common.queries.VdcQueryReturnValue;
import org.ovirt.engine.core.common.queries.VdcQueryType;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.compat.StringHelper;
import org.ovirt.engine.core.compat.Version;
import org.ovirt.engine.ui.frontend.AsyncQuery;
import org.ovirt.engine.ui.frontend.Frontend;
import org.ovirt.engine.ui.frontend.INewAsyncCallback;
import org.ovirt.engine.ui.uicommonweb.dataprovider.AsyncDataProvider;
import org.ovirt.engine.ui.uicommonweb.models.EntityModel;
import org.ovirt.engine.ui.uicommonweb.models.SystemTreeItemModel;
import org.ovirt.engine.ui.uicompat.ConstantsManager;
import org.ovirt.engine.ui.uicompat.UIConstants;

public class ExistingVmModelBehavior extends VmModelBehaviorBase
{
    private EditProfileBehavior networkBehavior = new EditProfileBehavior();

    protected VM vm;

    private List<VmNetworkInterface> networkInerfaces;
    private int hostCpu;
    private VDS runningOnHost;

    public ExistingVmModelBehavior(VM vm)
    {
        this.vm = vm;
    }

    public VM getVm() {
        return vm;
    }

    public void setVm(VM vm) {
        this.vm = vm;
    }

    @Override
    public void initialize(SystemTreeItemModel systemTreeSelectedItem) {
        super.initialize(systemTreeSelectedItem);
        getModel().getVmType().setIsChangable(true);
        getModel().getIsSoundcardEnabled().setIsChangable(true);

        AsyncQuery getVmNicsQuery = new AsyncQuery();
        getVmNicsQuery.asyncCallback = new INewAsyncCallback() {
            @Override
            public void onSuccess(Object model, Object result) {
                networkInerfaces = (List<VmNetworkInterface>) result;
                postNetworkInterfacesLoaded();
            }
        };
        AsyncDataProvider.getVmNicList(getVmNicsQuery, vm.getId());

        AsyncDataProvider.getWatchdogByVmId(new AsyncQuery(this.getModel(), new INewAsyncCallback() {
            @Override
            public void onSuccess(Object target, Object returnValue) {
                UnitVmModel model = (UnitVmModel) target;
                VdcQueryReturnValue val = (VdcQueryReturnValue) returnValue;
                @SuppressWarnings("unchecked")
                Collection<VmWatchdog> watchdogs = val.getReturnValue();
                for (VmWatchdog watchdog : watchdogs) {
                    model.getWatchdogAction().setSelectedItem(watchdog.getAction() == null ? null
                            : watchdog.getAction().name().toLowerCase());
                    model.getWatchdogModel().setSelectedItem(watchdog.getModel() == null ? ""
                            : watchdog.getModel().name());
                }
            }
        }), vm.getId());

    }

    private void postNetworkInterfacesLoaded() {
        AsyncDataProvider.getDataCenterById(new AsyncQuery(getModel(),
                new INewAsyncCallback() {
                    @Override
                    public void onSuccess(Object target, Object returnValue) {

                        UnitVmModel model = (UnitVmModel) target;
                        if (returnValue != null)
                        {
                            StoragePool dataCenter = (StoragePool) returnValue;
                            final List<StoragePool> dataCenters =
                                    new ArrayList<StoragePool>(Arrays.asList(new StoragePool[] { dataCenter }));

                            initClusters(dataCenters);
                        }
                        else
                        {
                            ExistingVmModelBehavior behavior = (ExistingVmModelBehavior) model.getBehavior();
                            VM currentVm = behavior.vm;
                            VDSGroup tempVar = new VDSGroup();
                            tempVar.setId(currentVm.getVdsGroupId());
                            tempVar.setName(currentVm.getVdsGroupName());
                            tempVar.setcompatibility_version(currentVm.getVdsGroupCompatibilityVersion());
                            tempVar.setStoragePoolId(currentVm.getStoragePoolId());
                            VDSGroup cluster = tempVar;
                            DataCenterWithCluster dataCenterWithCluster =
                                    new DataCenterWithCluster(null, cluster);
                            model.getDataCenterWithClustersList().setItems(Arrays.asList(dataCenterWithCluster));
                            model.getDataCenterWithClustersList().setSelectedItem(dataCenterWithCluster);
                            behavior.initTemplate();
                            behavior.initCdImage();
                            behavior.initSoundCard(vm.getId());
                        }

                    }
                },
                getModel().getHash()),
                vm.getStoragePoolId());
    }

    protected void initClusters(final List<StoragePool> dataCenters) {
        AsyncDataProvider.getClusterListByService(
                new AsyncQuery(getModel(), new INewAsyncCallback() {

                    @Override
                    public void onSuccess(Object target, Object returnValue) {
                        UnitVmModel model = (UnitVmModel) target;

                        List<VDSGroup> clusters = (List<VDSGroup>) returnValue;

                        List<VDSGroup> filteredClusters =
                                AsyncDataProvider.filterByArchitecture(clusters, vm.getClusterArch());

                        model.setDataCentersAndClusters(model,
                                dataCenters,
                                filteredClusters, vm.getVdsGroupId());
                        initTemplate();
                        initCdImage();
                        initSoundCard(vm.getId());
                    }
                }, getModel().getHash()),
                true, false);
    }

    @Override
    public void template_SelectedItemChanged()
    {
        // This method will be called even if a VM created from Blank template.

        // Update model state according to VM properties.
        getModel().getName().setEntity(vm.getName());
        getModel().getDescription().setEntity(vm.getVmDescription());
        getModel().getComment().setEntity(vm.getComment());
        getModel().getMemSize().setEntity(vm.getVmMemSizeMb());
        getModel().getMinAllocatedMemory().setEntity(vm.getMinAllocatedMem());
        getModel().getOSType().setSelectedItem(vm.getVmOsId());
        getModel().getDomain().setSelectedItem(vm.getVmDomain());
        getModel().getUsbPolicy().setSelectedItem(vm.getUsbPolicy());
        getModel().getNumOfMonitors().setSelectedItem(vm.getNumOfMonitors());
        getModel().getIsSingleQxlEnabled().setEntity(vm.getSingleQxlPci());
        getModel().getAllowConsoleReconnect().setEntity(vm.getAllowConsoleReconnect());
        getModel().setBootSequence(vm.getDefaultBootSequence());
        getModel().getIsHighlyAvailable().setEntity(vm.isAutoStartup());

        getModel().getTotalCPUCores().setEntity(Integer.toString(vm.getNumOfCpus()));
        getModel().getTotalCPUCores().setIsChangable(!vm.isRunning());

        getModel().getIsStateless().setEntity(vm.isStateless());
        getModel().getIsStateless().setIsAvailable(vm.getVmPoolId() == null);

        getModel().getIsRunAndPause().setEntity(vm.isRunAndPause());
        getModel().getIsRunAndPause().setIsChangable(!vm.isRunning());
        getModel().getIsRunAndPause().setIsAvailable(vm.getVmPoolId() == null);

        getModel().getIsSmartcardEnabled().setEntity(vm.isSmartcardEnabled());
        getModel().getIsDeleteProtected().setEntity(vm.isDeleteProtected());
        getModel().selectSsoMethod(vm.getSsoMethod());

        getModel().getNumOfSockets().setSelectedItem(vm.getNumOfSockets());
        getModel().getNumOfSockets().setIsChangable(isHotSetCpuSupported() || !vm.isRunning());

        getModel().getCoresPerSocket().setIsChangable(!vm.isRunning());

        getModel().getKernel_parameters().setEntity(vm.getKernelParams());
        getModel().getKernel_path().setEntity(vm.getKernelUrl());
        getModel().getInitrd_path().setEntity(vm.getInitrdUrl());

        getModel().getCustomProperties().setEntity(vm.getCustomProperties());
        getModel().getCustomPropertySheet().deserialize(vm.getCustomProperties());
        getModel().getCpuSharesAmount().setEntity(vm.getCpuShares());
        updateCpuSharesSelection();

        Frontend.getInstance().runQuery(VdcQueryType.GetWatchdog, new IdQueryParameters(getVm().getId()), new AsyncQuery(this,
                new INewAsyncCallback() {
                    @Override
                    public void onSuccess(Object model, Object returnValue) {
                        @SuppressWarnings("unchecked")
                        List<VmWatchdog> watchdogs =
                                ((VdcQueryReturnValue) returnValue).getReturnValue();
                        if (watchdogs.isEmpty()) {
                            getModel().getWatchdogAction().setSelectedItem(null);
                            getModel().getWatchdogModel().setSelectedItem(null);
                        } else {
                            VmWatchdog vmWatchdog = watchdogs.get(0);
                            getModel().getWatchdogAction().setSelectedItem(vmWatchdog.getAction() == null ? null
                                    : vmWatchdog.getAction().name().toLowerCase());
                            getModel().getWatchdogModel().setSelectedItem(vmWatchdog.getModel() == null ? ""
                                    : vmWatchdog.getModel().name());
                        }
                    }
                }));

        updateConsoleDevice(getVm().getId());

        updateVirtioScsiEnabled(getVm().getId());

        getModel().getVncKeyboardLayout().setSelectedItem(vm.getDefaultVncKeyboardLayout());

        Frontend.getInstance().runQuery(VdcQueryType.IsBalloonEnabled, new IdQueryParameters(getVm().getId()), new AsyncQuery(this,
                new INewAsyncCallback() {
                    @Override
                    public void onSuccess(Object model, Object returnValue) {
                        getModel().getMemoryBalloonDeviceEnabled().setEntity((Boolean) ((VdcQueryReturnValue)returnValue).getReturnValue());
                    }
                }
        ));

        if (vm.isInitialized())
        {
            UIConstants constants = ConstantsManager.getInstance().getConstants();
            getModel().getTimeZone().setChangeProhibitionReason(
                    constants.timeZoneCannotBeChangedAfterVMInit());
            getModel().getTimeZone().setIsChangable(false);
        }

        updateTimeZone(vm.getTimeZone());

        // Update domain list
        updateDomain();

        updateHostPinning(vm.getMigrationSupport());
        getModel().getHostCpu().setEntity(vm.isUseHostCpuFlags());

        // Storage domain and provisioning are not available for an existing VM.
        getModel().getStorageDomain().setIsChangable(false);
        getModel().getProvisioning().setIsAvailable(false);
        getModel().getProvisioning().setEntity(Guid.Empty.equals(vm.getVmtGuid()));

        // Select display protocol.
        for (EntityModel<DisplayType> model : getModel().getDisplayProtocol().getItems())
        {
            DisplayType displayType = model.getEntity();

            if (displayType == vm.getDefaultDisplayType())
            {
                getModel().getDisplayProtocol().setSelectedItem(model);
                break;
            }
        }

        getModel().getCpuPinning().setEntity(vm.getCpuPinning());
        initPriority(vm.getPriority());

        getModel().setSelectedMigrationDowntime(vm.getMigrationDowntime());

        if (isHotSetCpuSupported()) {
            // cancel related events while fetching data
            getModel().getTotalCPUCores().getEntityChangedEvent().removeListener(getModel());
            getModel().getCoresPerSocket().getSelectedItemChangedEvent().removeListener(getModel());
            getModel().getNumOfSockets().getSelectedItemChangedEvent().removeListener(getModel());

            AsyncDataProvider.getHostById(new AsyncQuery(this, new INewAsyncCallback() {
                @Override
                public void onSuccess(Object model, Object returnValue) {
                    ExistingVmModelBehavior existingVmModelBehavior = (ExistingVmModelBehavior) model;
                    runningOnHost = (VDS) returnValue;
                    hostCpu = calculateHostCpus();
                    existingVmModelBehavior.updateNumOfSockets();
                }
            }), vm.getRunOnVds());
        }
    }

    private int calculateHostCpus() {
        return  getModel().getSelectedCluster().getCountThreadsAsCores() ? runningOnHost.getCpuThreads() : runningOnHost.getCpuCores();
    }

    @Override
    public void postDataCenterWithClusterSelectedItemChanged()
    {
        updateDefaultHost();
        updateCustomPropertySheet();
        updateNumOfSockets();
        updateQuotaByCluster(vm.getQuotaId(), vm.getQuotaName());
        updateCpuPinningVisibility();
        updateMemoryBalloon();
        updateCpuSharesAvailability();
        updateNetworkInterfaces(networkBehavior, networkInerfaces);
        updateVirtioScsiAvailability();
        updateOSValues();
    }

    @Override
    protected void changeDefualtHost()
    {
        super.changeDefualtHost();
        doChangeDefautlHost(vm.getDedicatedVmForVds());
    }

    @Override
    public void defaultHost_SelectedItemChanged()
    {
        updateCdImage();
    }

    @Override
    public void provisioning_SelectedItemChanged()
    {
    }

    @Override
    public void updateMinAllocatedMemory()
    {
        DataCenterWithCluster dataCenterWithCluster = getModel().getDataCenterWithClustersList().getSelectedItem();
        if (dataCenterWithCluster == null) {
            return;
        }

        VDSGroup cluster = dataCenterWithCluster.getCluster();

        if (cluster == null)
        {
            return;
        }

        if (getModel().getMemSize().getEntity() < vm.getVmMemSizeMb())
        {
            double overCommitFactor = 100.0 / cluster.getmax_vds_memory_over_commit();
            getModel().getMinAllocatedMemory()
                    .setEntity((int) ((Integer) getModel().getMemSize().getEntity() * overCommitFactor));
        }
        else
        {
            getModel().getMinAllocatedMemory().setEntity(vm.getMinAllocatedMem());
        }
    }

    public void initTemplate()
    {
        setupTemplate(vm, getModel().getTemplate());
    }

    public void initCdImage()
    {
        getModel().getCdImage().setSelectedItem(vm.getIsoPath());

        boolean hasCd = !StringHelper.isNullOrEmpty(vm.getIsoPath());
        getModel().getCdImage().setIsChangable(hasCd);
        getModel().getCdAttached().setEntity(hasCd);

        updateCdImage();
    }

    @Override
    public void numOfSocketChanged() {
        if (isHotSetCpuSupported()) {
            int numOfSockets = extractIntFromListModel(getModel().getNumOfSockets());
            int coresPerSocket = vm.getCpuPerSocket();
            getModel().getTotalCPUCores().setEntity(Integer.toString(numOfSockets * coresPerSocket));
        } else {
            super.numOfSocketChanged();
        }
    }

    @Override
    public void totalCpuCoresChanged() {
        if (isHotSetCpuSupported()) {
            if (runningOnHost == null) {
                return; //async call didn't return with the host yet
            }
            // must not change the num of cpu per socket so the list has only 1 item
            List<Integer> coresPerSockets = Arrays.asList(new Integer[]{vm.getCpuPerSocket()});

            getModel().getCoresPerSocket().setItems(coresPerSockets);
            getModel().getNumOfSockets().setItems(createSocketsRange());

            getModel().getCoresPerSocket().setSelectedItem(vm.getCpuPerSocket());
            getModel().getNumOfSockets().setSelectedItem(vm.getNumOfSockets());

            getModel().getNumOfSockets().getSelectedItemChangedEvent().addListener(getModel());
            numOfSocketChanged();
        } else {
            super.totalCpuCoresChanged();
        }
    }

    /**
     *  span a list of all possible sockets values
     */
    private List<Integer> createSocketsRange() {
        List<Integer> res = new ArrayList<Integer>();
        int maxHostCpu = getHostCpu();
        int cpusPerSockets = vm.getCpuPerSocket();

        for (int i = 1; i <= maxHostCpu; i++) {
            // sockets stepping must not exceed the host maximum
            if (i * cpusPerSockets <= maxHostCpu) {
                res.add(i);
            }
        }
        return res;
    }

    public boolean isHotSetCpuSupported() {
        VDSGroup selectedCluster = getModel().getSelectedCluster();
        Version clusterVersion = selectedCluster.getcompatibility_version();
        Boolean hotplugEnabled = (Boolean) AsyncDataProvider.getConfigValuePreConverted(ConfigurationValues.HotPlugEnabled, clusterVersion.getValue());
        boolean hotplugCpuSupported = Boolean.parseBoolean(((Map<String, String>) AsyncDataProvider.getConfigValuePreConverted(ConfigurationValues.HotPlugCpuSupported,
                clusterVersion.getValue())).get(selectedCluster.getArchitecture().name()));

        return getVm().getStatus() == VMStatus.Up && hotplugEnabled && hotplugCpuSupported;
    }

    public int getHostCpu() {
        return hostCpu;
    }
}

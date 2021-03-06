package org.ovirt.engine.ui.uicommonweb.models.vms.hostdev;

import java.util.Collection;
import java.util.List;

import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.ui.frontend.AsyncCallback;
import org.ovirt.engine.ui.uicommonweb.Linq;
import org.ovirt.engine.ui.uicommonweb.dataprovider.AsyncDataProvider;
import org.ovirt.engine.ui.uicommonweb.models.ListModel;
import org.ovirt.engine.ui.uicommonweb.models.Model;

public class ModelWithPinnedHost extends Model {

    private ListModel<VDS> pinnedHost;

    private VM vm;

    public ModelWithPinnedHost() {
        setPinnedHost(new ListModel<VDS>());
    }

    public void init(VM vm) {
        this.vm = vm;
    }

    public ListModel<VDS> getPinnedHost() {
        return pinnedHost;
    }

    private void setPinnedHost(ListModel<VDS> pinnedHost) {
        this.pinnedHost = pinnedHost;
    }

    public VM getVm() {
        return vm;
    }

    protected void initHosts() {
        startProgress();
        AsyncDataProvider.getInstance().getHostListByClusterId(new AsyncQuery<>(new AsyncCallback<List<VDS>>() {
            @Override
            public void onSuccess(List<VDS> hosts) {
                getPinnedHost().setItems(filterHostDevicePassthroughCapableHosts(hosts));
                stopProgress();
                selectCurrentPinnedHost();
            }
        }), vm.getClusterId());
    }

    private void selectCurrentPinnedHost() {
        VDS host = Linq.findHostByIdFromIdList(getPinnedHost().getItems(), vm.getDedicatedVmForVdsList());
        if (host != null) {
            getPinnedHost().setSelectedItem(host);
        }
    }

    private Collection<VDS> filterHostDevicePassthroughCapableHosts(Collection<VDS> hosts) {
        return Linq.where(hosts, new Linq.IPredicate<VDS>() {
            @Override
            public boolean match(VDS host) {
                return host.isHostDevicePassthroughEnabled();
            }
        });
    }
}

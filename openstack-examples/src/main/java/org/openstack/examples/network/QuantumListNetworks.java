package org.openstack.examples.network;

import org.openstack.base.client.OpenStackSimpleTokenProvider;
import org.openstack.examples.ExamplesConfiguration;
import org.openstack.keystone.Keystone;
import org.openstack.keystone.model.Access;
import org.openstack.keystone.model.Tenants;
import org.openstack.keystone.model.authentication.TokenAuthentication;
import org.openstack.keystone.model.authentication.UsernamePassword;
import org.openstack.keystone.utils.KeystoneUtils;
import org.openstack.quantum.Quantum;
import org.openstack.quantum.api.NetworksResource;
import org.openstack.quantum.model.Network;
import org.openstack.quantum.model.Networks;

public class QuantumListNetworks {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Keystone keystone = new Keystone(ExamplesConfiguration.KEYSTONE_AUTH_URL);
		// access with unscoped token
		Access access = keystone.tokens().authenticate(
				new UsernamePassword(ExamplesConfiguration.KEYSTONE_USERNAME, ExamplesConfiguration.KEYSTONE_PASSWORD))
				.execute();
		// use the token in the following requests
		keystone.setTokenProvider(new OpenStackSimpleTokenProvider(access.getToken().getId()));

		Tenants tenants = keystone.tenants().list().execute();
		// try to exchange token using the first tenant
		if (tenants.getList().size() > 0) {
			// access with tenant
			access = keystone.tokens().authenticate(new TokenAuthentication(access.getToken().getId())).withTenantId(tenants.getList().get(0).getId()).execute();

			Quantum quantum = new Quantum(KeystoneUtils.findEndpointURL(access.getServiceCatalog(), "network",	null, "public"));
			quantum.setTokenProvider(new OpenStackSimpleTokenProvider(access.getToken().getId()));

			Networks networks = quantum.networks().list().execute();
			for (Network network : networks) {
				System.out.println(network);
			}
		} else {
			System.out.println("No tenants found!");
		}
	}
}

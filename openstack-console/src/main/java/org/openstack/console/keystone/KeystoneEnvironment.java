package org.openstack.console.keystone;

import org.apache.commons.cli.CommandLine;
import org.openstack.base.client.OpenStackSimpleTokenProvider;
import org.openstack.console.Command;
import org.openstack.console.Console;
import org.openstack.console.Environment;
import org.openstack.keystone.Keystone;
import org.openstack.keystone.api.Authenticate;
import org.openstack.keystone.model.Access;
import org.openstack.keystone.model.authentication.UsernamePassword;

public class KeystoneEnvironment extends Environment {
	
	public final Keystone CLIENT;
	
	public static final Command KEYSTONE = new Command("keystone") {
		
		@Override
		public void execute(Console console, CommandLine args) {
			
			Keystone client = new Keystone(console.getProperty("keystone.endpoint"));
			
			Access access = client.tokens()
				.authenticate(new UsernamePassword(
					console.getProperty("keystone.username"), 
					console.getProperty("keystone.password")
				))
				.withTenantName(console.getProperty("keystone.tenant_name"))
				.execute();
					
			client.setTokenProvider(new OpenStackSimpleTokenProvider(access.getToken().getId()));
			
			KeystoneEnvironment environment = new KeystoneEnvironment(console.getEnvironment(), client);
			
			environment.register(new KeystoneTenantList());
			environment.register(new KeystoneTenantCreate());
			environment.register(new KeystoneTenantDelete());
			environment.register(new KeystoneUserList());
			environment.register(new KeystoneUserCreate());
			environment.register(new KeystoneUserDelete());
			environment.register(new KeystoneRoleList());
			environment.register(new KeystoneRoleDelete());
			environment.register(new KeystoneServiceList());	
			console.setEnvironment(environment);
		}
		
	};
	
	public KeystoneEnvironment(Environment parent, Keystone client) {
		super(parent);
		CLIENT = client;
	}

	/* (non-Javadoc)
	 * @see org.woorea.wsh.Environment#getPrompt()
	 */
	@Override
	public String getPrompt() {
		return "keystone> ";
	}
	
}


package acme.features.authenticated.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Authenticated;
import acme.client.data.accounts.Principal;
import acme.client.data.accounts.UserAccount;
import acme.client.data.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractService;
import acme.roles.Client;

@Service
public class AuthenticatedClientCreateService extends AbstractService<Authenticated, Client> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedClientRepository repository;

	// AbstractService inteface -----------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !this.getRequest().getPrincipal().hasRole(Client.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Client object;
		Principal principal;
		int userAccountId;
		UserAccount userAccount;

		principal = this.getRequest().getPrincipal();
		userAccountId = principal.getAccountId();
		userAccount = this.repository.findOneUserAccountById(userAccountId);

		object = new Client();
		object.setUserAccount(userAccount);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Client object) {
		assert object != null;

		super.bind(object, "qualifications", "skills");
	}

	@Override
	public void validate(final Client object) {
		assert object != null;
	}

	@Override
	public void perform(final Client object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Client object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "qualifications", "skills");

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}

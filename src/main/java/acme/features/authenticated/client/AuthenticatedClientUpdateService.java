
package acme.features.authenticated.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Authenticated;
import acme.client.data.accounts.Principal;
import acme.client.data.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.roles.Client;
import acme.roles.TypeClient;

@Service
public class AuthenticatedClientUpdateService extends AbstractService<Authenticated, Client> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedClientRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Client object;
		Principal principal;
		int userAccountId;

		principal = super.getRequest().getPrincipal();
		userAccountId = principal.getAccountId();
		object = this.repository.findOneClientByUserAccountId(userAccountId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Client object) {
		assert object != null;

		super.bind(object, "identification", "companyName", "type", "email", "link");
	}

	@Override
	public void validate(final Client object) {
		assert object != null;
		if (!super.getBuffer().getErrors().hasErrors("identification")) {
			Client existing;

			existing = this.repository.findOneClientByIdentification(object.getIdentification());
			super.state(existing == null || existing.getId() == object.getId(), "code", "authenticated.client.form.error.duplicated");
		}
	}

	@Override
	public void perform(final Client object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Client object) {
		assert object != null;

		SelectChoices choices;

		Dataset dataset;

		choices = SelectChoices.from(TypeClient.class, object.getType());

		dataset = super.unbind(object, "identification", "companyName", "type", "email", "link");
		dataset.put("type", choices.getSelected().getKey());
		dataset.put("types", choices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}


package acme.features.manager.userstory;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.services.AbstractService;
import acme.entities.assignations.Assignation;
import acme.entities.userstories.UserStory;
import acme.roles.Manager;

@Service
public class ManagerUserStoryDeleteService extends AbstractService<Manager, UserStory> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerUserStoryRepository repository;

	// AbstractService<Manager, UserStory> ---------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		UserStory userStory;
		Manager manager;

		masterId = super.getRequest().getData("id", int.class);
		userStory = this.repository.findOneUserStoryById(masterId);
		manager = userStory == null ? null : userStory.getManager();
		status = userStory != null && userStory.isDraftMode() && super.getRequest().getPrincipal().hasRole(manager);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		UserStory object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneUserStoryById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final UserStory object) {
		assert object != null;

		super.bind(object, "title", "description", "estimatedCost", "acceptanceCriteria", "priority", "link");
	}

	@Override
	public void validate(final UserStory object) {
		assert object != null;
	}

	@Override
	public void perform(final UserStory object) {
		assert object != null;

		Collection<Assignation> a;

		a = this.repository.findAssignationsByUserStoryId(object.getId());

		this.repository.deleteAll(a);
		this.repository.delete(object);
	}

	//	@Override
	//	public void unbind(final UserStory object) {
	//		assert object != null;
	//
	//		SelectChoices choices;
	//		Dataset dataset;
	//
	//		choices = SelectChoices.from(Priority.class, object.getPriority());
	//
	//		dataset = super.unbind(object, "title", "description", "estimatedCost", "acceptanceCriteria", "link", "draftMode");
	//		dataset.put("priority", choices.getSelected().getKey());
	//		dataset.put("priorities", choices);
	//
	//		super.getResponse().addData(dataset);
	//	}

}

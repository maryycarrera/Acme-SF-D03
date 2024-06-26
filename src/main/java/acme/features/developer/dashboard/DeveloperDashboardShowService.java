
package acme.features.developer.dashboard;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Principal;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.trainingmodules.TrainingModule;
import acme.entities.trainingsessions.TrainingSession;
import acme.forms.DeveloperDashboard;
import acme.roles.Developer;

@Service
public class DeveloperDashboardShowService extends AbstractService<Developer, DeveloperDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private DeveloperDashboardRepository dashboardRepository;


	@Override
	public void authorise() {
		boolean status;

		Principal principal = super.getRequest().getPrincipal();
		int id = principal.getAccountId();
		Developer developer = this.dashboardRepository.findDeveloperById(id);
		status = developer != null && principal.hasRole(Developer.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		final Principal principal = super.getRequest().getPrincipal();
		int id = principal.getAccountId();
		DeveloperDashboard developerDashboard = new DeveloperDashboard();
		Collection<TrainingModule> modules = this.dashboardRepository.findAllTrainingModulesByDeveloperId(id);
		Collection<TrainingSession> sessions = this.dashboardRepository.findAllTrainingSessionsByDeveloperId(id);

		developerDashboard.setTotalNumberTrainingSessionsWithLink(0);
		developerDashboard.setTotalNumberTrainingModulesWithUpdateMoment(0);
		developerDashboard.setAverageTimeTrainingModules(0.);
		developerDashboard.setDeviationTimeTrainingModules(0.);
		developerDashboard.setMinimumTimeTrainingModules(0);
		developerDashboard.setMaximumTimeTrainingModules(0);

		if (!modules.isEmpty()) {
			developerDashboard.setTotalNumberTrainingModulesWithUpdateMoment(this.dashboardRepository.totalTrainingModulesWithUpdateMoment(id));
			developerDashboard.setAverageTimeTrainingModules(this.dashboardRepository.averageTrainingModulesTime(id));
			developerDashboard.setDeviationTimeTrainingModules(this.dashboardRepository.deviatonTrainingModulesTime(id));
			developerDashboard.setMinimumTimeTrainingModules(this.dashboardRepository.minimumTrainingModulesTime(id));
			developerDashboard.setMaximumTimeTrainingModules(this.dashboardRepository.maximumTrainingModulesTime(id));
		}

		if (!sessions.isEmpty())
			developerDashboard.setTotalNumberTrainingSessionsWithLink(this.dashboardRepository.totalTrainingSessionsWithLink(id));

		super.getBuffer().addData(developerDashboard);

	}

	@Override
	public void unbind(final DeveloperDashboard object) {
		Dataset dataset;

		dataset = super.unbind(object, "totalNumberTrainingModulesWithUpdateMoment", "totalNumberTrainingSessionsWithLink", "averageTimeTrainingModules", "deviationTimeTrainingModules", "minimumTimeTrainingModules", "maximumTimeTrainingModules");

		super.getResponse().addData(dataset);
	}

}

clear; close all;

load("results_converted/0.2_at_arrival.mat");

drones = data.res.drones;

priority = drones(drones.is_priority > 0, :);
non_priority = drones(drones.is_priority == 0, :);

display("Average")
display(mean(drones.delay));

display("Average priority")
display(mean(priority.delay));

display("Average non-priority")
display(mean(non_priority.delay));


display("Average delay due to priority")
display(mean(non_priority.delay_due_priority(non_priority.delay_due_priority > 0)));

display("Num delayed due to priority")
display(sum(non_priority.delay_due_priority > 0));
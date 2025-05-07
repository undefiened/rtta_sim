close all; clear;

N = 20000;
r = 150;
wc = '0.0';
speeds = {'25.0', '24.7481', '23.9678', '22.5695', '20.3137'};
wind_speeds = [0, 5, 10, 15, 20];


business_coeff = 0.1;
leisure_coeff = business_coeff;

figure;
hold on;

square_delays = false;
cap_rtta_subtraction = false;

for speed_i=1:length(speeds)
    speed = speeds{speed_i};
    file_part = ['RTTA_', num2str(N), '_', num2str(r), '.0_', wc, '_', speed];
    data = load(['./results_converted/', file_part, '.mat']);
    data = data.data;
    
    
    RTTAs = [];
    avg_delays = [];
    median_delays = [];
    max_delays = [];
    avg_costs = [];
    median_costs = [];
    num_big_costs = [];
    num_big_delays = [];
    gini_indices = [];
    geomean_indices = [];
    per_10 = [];
    per_90 = [];
    per_95 = [];
    per_99 = [];
    stds = [];
    
    leisure_coeff = business_coeff;
    for res_i=1:length(data.res)
        res = data.res(res_i);
        
        res.drones = res.drones(res.drones.cancelled_after_RTTA == 0, :);
        RTTA = res.RTTA./60;
        drones = res.drones;

        RTTAs = [RTTAs, RTTA];

        drone_delays = [drones.delay];
        drone_delays = drone_delays./60;
        
        if square_delays
            drone_delays = drone_delays .* drone_delays;
        end
        
        drone_types = [drones.type];

        avg_delay = mean(drone_delays);
        avg_delays = [avg_delays, avg_delay];
        median_delays = [median_delays, median(drone_delays)];
        max_delays = [max_delays, max(drone_delays)];
        per_10 = [per_10, prctile(drone_delays, 10)];
        per_90 = [per_90, prctile(drone_delays, 90)];
        per_95 = [per_95, prctile(drone_delays, 95)];
        per_99 = [per_99, prctile(drone_delays, 99)];
        
        gini_indices = [gini_indices, ginicoeff(drone_delays)];
        drone_delays_without_0 = drone_delays;
        drone_delays_without_0(drone_delays_without_0 == 0) = 1;
        
        geomean_indices = [geomean_indices, geomean(drone_delays_without_0)/mean(drone_delays_without_0)];

        is_business = mod(drone_types, 2) == 0;

        actual_rtta = ([drones.actual_start] - [drones.scheduling_time])./60;
        scheduling_delay = abs([drones.scheduling_time] - [drones.intent_arrival])./60;
        
        if cap_rtta_subtraction
            actual_rtta = min(actual_rtta, max_rtta_subtraction);
        end

        rtta_adjusted_delays = is_business .* (drone_delays - actual_rtta.*business_coeff) + ...
                                (1 - is_business) .* (drone_delays - actual_rtta.*leisure_coeff);

        rtta_adjusted_delays = max(0, rtta_adjusted_delays);

        avg_costs = [avg_costs, mean(rtta_adjusted_delays)];
        median_costs = [median_costs, median(rtta_adjusted_delays)];
        num_big_costs = [num_big_costs, sum(rtta_adjusted_delays > 15)];
        num_big_delays = [num_big_delays, sum(drone_delays > 15)];
        stds = [stds, std(drone_delays)];
    end
    
    disp_name = [num2str(wind_speeds(speed_i)), ' m/s'];

    plot(RTTAs, avg_costs, 'DisplayName', disp_name);
%     [min_avg_cost, min_avg_cost_ind] = min(avg_costs);
    
%     h = plot([RTTAs(min_avg_cost_ind), ], [avg_costs(min_avg_cost_ind), ],'r*');
%     set(get(get(h, 'Annotation'), 'LegendInformation'), 'IconDisplayStyle', 'off');
    % plot(RTTAs, RTTAs);

    % axis equal;
end

xlabel('RTTA (min)');
ylabel('Mean cost');
legend show;
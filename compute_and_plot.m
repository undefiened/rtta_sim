clear; close all;

% load("./results_converted/RTTA_20000_150.0.mat");
% load("./results_converted/RTTA_10000_150.0_0.0083.mat");
load("./results_converted/RTTA_20000_150.0_0.0.mat");
close all;

coeffs = [0, 0.005, 0.01, 0.015, 0.02, 0.04, 0.06, 0.1, 0.15, 0.25, 0.5];
coeffs = [0, 0.02, 0.04, 0.06, 0.08, 0.1, 0.15, 0.25, 0.5];

business_coeff = 0.01;
leisure_coeff = business_coeff; 0.01;

cap_rtta_subtraction = false;
max_rtta_subtraction = 60;

square_delays = false;

figure('Position', [100, 500, 560, 420]);
hold on;

% for res_i=1:length(data.res)
%     distances = [];
%     fromCoords = [];
%     toCoords = [];
%     
%     for i = 1:height(data.res(res_i).drones)
%         fromCoord = cell2mat(data.res(res_i).drones.from(i));
%         fromCoords = [fromCoords fromCoord];
%         toCoord = cell2mat(data.res(res_i).drones.to(i));
%         toCoords = [toCoords toCoord];
%         distances(i) = sqrt(sum((fromCoord - toCoord).^2));
%     end
% 
%     data.res(res_i).drones.distances = distances';
% end

for business_coeff=coeffs
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


    plot(RTTAs, avg_costs, 'DisplayName', num2str(business_coeff));
    [min_avg_cost, min_avg_cost_ind] = min(avg_costs);
    
%     h = plot([RTTAs(min_avg_cost_ind), ], [avg_costs(min_avg_cost_ind), ],'r*');
%     set(get(get(h, 'Annotation'), 'LegendInformation'), 'IconDisplayStyle', 'off');
    % plot(RTTAs, RTTAs);

    % axis equal;
end

xlabel('RTTA (min)');
ylabel('Mean cost');
legend show;

figure('Position', [700, 500, 560, 420]);
hold on;
% plot(RTTAs, median_delays, 'DisplayName', 'Median');
plot(RTTAs, avg_delays, 'DisplayName', 'Mean');
% plot(RTTAs, max_delays, 'DisplayName', 'Max');
% plot(RTTAs, per_10, 'DisplayName', '10 percentile');
% plot(RTTAs, per_90, 'DisplayName', '90 percentile');
% plot(RTTAs, per_95, 'DisplayName', '95 percentile');
% plot(RTTAs, per_99, 'DisplayName', '99 percentile');
% errorbar(RTTAs, avg_delays, stds);
% plot(RTTAs, RTTAs);
xlabel('RTTA (min)');
ylabel('Delay (min)');
% legend show;
% axis equal;
% errorbar


figure;
plot(RTTAs, gini_indices);
xlabel('RTTA (min)');
ylabel('Gini coefficient (higher → more inequality)');

% 
% 
% figure;
% plot(RTTAs, num_big_costs);
% xlabel('RTTA (min)');
% ylabel('Number of drones experienced cost > 15');
% 
% 
figure('Position', [1300, 500, 560, 420]);
plot(RTTAs, num_big_delays);
xlabel('RTTA (min)');
ylabel('Number of drones experienced delay > 15 min');
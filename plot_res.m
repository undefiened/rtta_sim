close all; clear;

load('delays_300_m.mat');

% Plot boxplot

% figure('Renderer', 'painters', 'Position', [10 10 1900 900])
% boxplot(all_results'/60,'whisker', 1000, 'labels', RTTAs/60);
% ylabel('Delays (min)');
% xlabel('RTTA (min)');
% title(['Delays distribution (r=' num2str(r) 'm)']);
% saveas(gcf, 'delays_distribution.png')



% Plot number of approved drones
cancelled_approved = [0, 0, 2, 4, 9, 10, 11, 12, 14, 14, 17, 18, 18, 19, 20, 23, 27, 29, 31, 31, 31, 36, 38, 39, 42, 45, 48, 50, 50, 53, 54, 56, 58, 59, 60, 61, 62, 62, 65, 65, 65, 66, 67, 70, 70, 70, 70, 70];
all_drones = size(all_results, 2);

figure;
plot(RTTAs/60, cancelled_approved+all_drones);
ylabel('# of approved drones');
xlabel('RTTA (min)');
title('Total number of approved drones');
saveas(gcf, 'approved_drones.png')

% Plot numbers of overdelayed drones
delays = 10:5:35;

labels = {};

figure;
hold on;

for i=1:length(delays)
    delay = delays(i);
    
    labels{i, 1} = [num2str(delay) ' min'];
    
    plot(RTTAs/60, sum((all_results > delay*60)'));
end

ylabel('# of delayed drones');
xlabel('RTTA (min)');
title('Number of drones delayed more than certain time');
legend(labels)
saveas(gcf, 'delayed_drones.png')
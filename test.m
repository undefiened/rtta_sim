data1 = [data.res(1).drones.delay];
% data1 = data1(data1 > 0);
data2 = [data.res(48).drones.delay];
% data2 = data2(data2 > 0);

% figure; 
% hold on;
% bins = [-1:100:25000];
% histogram(data1, bins, 'DisplayName', 'd1');
% histogram(data2, bins, 'DisplayName', 'd2');
% legend show;

% Define the bin edges
bins = -1:100:25000;

% Calculate bin counts for data1
[counts1, edges1] = histcounts(data1, bins);

% Calculate bin counts for data2
[counts2, edges2] = histcounts(data2, bins);

% Calculate bin centers
binCenters = edges1(1:end-1) + diff(edges1)/2;

% Plot the histograms as line plots
figure;
hold on;
plot(binCenters, counts1, 'DisplayName', 'd1');
plot(binCenters, counts2, 'DisplayName', 'd2');

% Add labels, legend, and title
xlabel('Delay');
ylabel('Frequency');
title('Line Histogram');
legend show;
hold off;
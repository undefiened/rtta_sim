close all; clear;

Ns = [1000, 5000, 10000, 20000, 30000, 40000];
rs = [10, 50, 100, 150, 200];

for N=Ns
    for r=rs
        filename = ['./results/RTTA_', num2str(N), '_', num2str(r), '.0.json'];

        data = jsondecode(fileread(filename));

        for i=1:length(data.res)
            start_times = [data.res(i).drones.desired_start];
            [~, sortIdx] = sort(start_times);
            data.res(i).drones = struct2table(data.res(i).drones(sortIdx));
        end

        save(['./results_converted/RTTA_', num2str(N), '_', num2str(r), '.0.mat'], "data", "-v7.3");
    end
end
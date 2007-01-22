function TestFunctions
   
    function [xm,ym,zm]=FindMaxBruteForce(res,f)
       disp(f);
       zmax = -100000000;
       xmax= -2;
       ymax= -2;
       for x = -1.0: res: 1.0
        for y = -1.0: res: 1.0
           z = f(x,y);
           if (z>zmax)
               zmax=z;
               xmax=x;
               ymax=y;               
           end
        end  
       end 
       xm=xmax
       ym=ymax
       zm=zmax
    end    
         
     dist = @(x,y) sqrt(x^2+y^2);
     simplequad = @(x,y) 1/(1+((2*x)^2+(2*y)^2));
     spike = @(x,y) 1-(x^2 + y^2)^0.25;
     sync = @(x,y) (1/9.9998)*sin(10*dist(x,y))/((dist(x,y)+0.0000000001)); 
     gridspike = @(x,y) spike(x,y)*(cos(10*x)*cos(10*y) -(x+y));
     flattop = @(x,y) 0.05*(cos(10*x)*cos(10*y))+ 0.95/(1+dist(2*x,2*y)^20); 
     multiquad = @(x,y)      (1/1.1439)*(simplequad(x-0.5,y-0.8) +0.8*simplequad(x+0.7,y+0.2) +0.5*simplequad(x+0.7,y-0.4)) ; 
     multispike = @(x,y)     (1/0.7378)*(spike(x-0.5,y-0.8)      +0.8*spike(x+0.7,y+0.2)      +0.5*spike(x+0.7,y-0.4));
     multisync = @(x,y)      (1/1.0084)*(sync(x-0.5,y-0.8)       +0.8*sync(x+0.7,y+0.2)       +0.5*sync(x+0.7,y-0.4))
     multigridspike = @(x,y) (1/1.7153)*(gridspike(x-0.5,y-0.8)  +0.8*gridspike(x+0.7,y+0.2)  +0.5*gridspike(x+0.7,y-0.4))
     multiflattop = @(x,y)   (1/1.2675)*(flattop(x-0.5,y-0.8)    +0.8*flattop(x+0.7,y+0.2)    +0.5*flattop(x+0.7,y-0.4))
     megamix = @(x,y)        (1/2.9213)*(multiquad(1-x,y) + multiflattop(x,1-y) +  multispike(1-x,1-y) + multisync(1-2*x^2,y)+ multigridspike(x,1-2*y^2))

     figure(1);ezsurfc(simplequad,[-1, 1, -1, 1])
     figure(2);ezsurfc(spike,[-1, 1, -1, 1])
     figure(3);ezsurfc(sync,[-1, 1, -1, 1])
     figure(4);ezsurfc(gridspike,[-1, 1, -1, 1])
     figure(5);ezsurfc(flattop,[-1, 1, -1, 1]) 
     figure(6);ezsurfc(multiquad,[-1, 1, -1, 1]) 
     figure(7);ezsurfc(multispike,[-1, 1, -1, 1])  
     figure(8);ezsurfc(multisync,[-1, 1, -1, 1])  
     figure(9);ezsurfc(multigridspike,[-1, 1, -1, 1])  
     figure(10);ezsurfc(multiflattop,[-1, 1, -1, 1])  
     figure(11);ezsurfc(megamix,[-1, 1, -1, 1]) 
        
     res = 0.001;
     %FindMaxBruteForce(res,simplequad);     % Max: (0, 0) -> 1
     %FindMaxBruteForce(res,spike);          % Max: (0, 0) -> 1
     %FindMaxBruteForce(res,sync);           % Max: (0, 0) -> 9.9998
     %FindMaxBruteForce(res,gridspike);      % Max: (0, 0) -> 1
     %FindMaxBruteForce(res,flattop);        % Max: (0, 0) -> 1
     %FindMaxBruteForce(res,multiquad);      % Max: (0.480, 0.789) -> 1.1439
     %FindMaxBruteForce(res,multispike);     % Max: (0.5000, 0.8000) -> 0.7378
     %FindMaxBruteForce(res,multisync);      % Max: (0.499, 0.794) -> 1.0084
     %FindMaxBruteForce(res,multigridspike); % Max: (0.500, 0.800) -> 1.7153
     %FindMaxBruteForce(res,multiflattop);   % Max: (-0.9280, 0.0930) -> 1.2675
     %FindMaxBruteForce(res,megamix);        % Max: (0.5130, 0.6480) -> 2.9213
         
end





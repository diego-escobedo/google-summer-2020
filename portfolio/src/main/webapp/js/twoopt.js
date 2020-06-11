function twoopt(distmat, iterlimit) {
    const N = distmat.length;
    var iter = 0;
    var existing_route = greedyRoute(distmat);
    existing_route.push(0);
    //repeat until no improvement is made {
    var best_distance = Infinity;
    while (iter < iterlimit) {
        //start_again:
        best_distance = calculateTotalDistance(existing_route,distmat);
        var better_found = false;
        for (i = 1; i < existing_route.length - 2; i++) {
            for (k = i + 1; k < existing_route.length - 1; k++) {
                var new_route = swap(existing_route, i, k);
                var new_distance = calculateTotalDistance(new_route, distmat);
                if (new_distance < best_distance) {
                    existing_route = new_route;
                    best_distance = new_distance;
                    better_found = true;
                    break;
                }
            }
            if (better_found === true) {
                break;
            }
        }
        if (better_found === true) {
                continue;
            }
        else {
            break;
        }
    }
    return existing_route;
}

function calculateTotalDistance(route,distmat) {
    
    var cost = 0;
    for (rte_ord_ind = 0; rte_ord_ind < route.length - 1; rte_ord_ind++) {
        const cur_city_index = route[rte_ord_ind];
        const next_city_index = route[rte_ord_ind+1];
        cost += distmat[cur_city_index][next_city_index];
    }
    //now get the last one
    const cur_city_index = route[route.length - 1];
    const next_city_index = route[0];
    cost += distmat[cur_city_index][next_city_index];
    //return the cost
    return cost;
}

function swap(route,i,k) {
    var new_route = []
    for (rte_ind = 0; rte_ind < i; rte_ind++) {
        new_route.push(route[rte_ind]);
    }
    for (rte_ind = k; rte_ind >= i; rte_ind--) {
        new_route.push(route[rte_ind]);
    }
    for (rte_ind = k+1; rte_ind < route.length; rte_ind++) {
        new_route.push(route[rte_ind]);
    }
    return new_route;
}

function greedyRoute(distmat) {
    var rte = [0];
    while (rte.length < distmat.length) {
        var cur_city = rte[rte.length-1];
        var min_length = Infinity;
        var min_index = 0;
        for (i = 0; i < distmat[cur_city].length; i++) {
            if (distmat[cur_city][i] < min_length && i !== cur_city && !(rte.includes(i))) {
                min_length = distmat[cur_city][i];
                min_index = i;
            }
        }
        rte.push(min_index)
    }
    return rte
}
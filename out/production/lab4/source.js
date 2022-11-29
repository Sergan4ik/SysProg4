// take input
const base = prompt('Enter a base: ');
const power = prompt('Enter a power: ');

let curPower = 1
let res = 1;

if (power == 0){
    console.log(res);
}
else {
    res = base;
    while (curPower < power) {
        if (curPower * 2 <= power){
            curPower *= 2;
            res *= res;
        }
        else{
            curPower++;
            res *= base;
        }
    }
    console.log(res);
}

